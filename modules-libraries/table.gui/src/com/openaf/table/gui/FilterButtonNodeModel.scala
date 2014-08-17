package com.openaf.table.gui

import javafx.beans.property.{SimpleBooleanProperty, Property}
import com.openaf.table.lib.api._
import java.util.Locale
import javafx.collections.ObservableList
import scala.collection.mutable
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.lang.{Boolean => JBoolean}

class FilterButtonNodeModel[T](field:Field[T], tableData:Property[TableData], locale:Property[Locale]) {
  private val allBooleanProperty = new SimpleBooleanProperty
  private var allShouldChange = true
  allBooleanProperty.addListener(new ChangeListener[JBoolean] {
    def changed(value:ObservableValue[_ <:JBoolean], oldValue:JBoolean, newValue:JBoolean) {
      if (allShouldChange) {propertyLookUp.values.foreach(_.setValue(newValue))}
    }
  })
  val values = tableData.getValue.tableValues.fieldValues.values(field)
  val numValues = values.length
  private val defaultRenderer = tableData.getValue.defaultRenderers(field).asInstanceOf[Renderer[T]]
  private val valueLookUp = tableData.getValue.tableValues.valueLookUp(field.id)
  private val propertyLookUp = {
    val lookup = new mutable.LongMap[SimpleBooleanProperty]
    var i = 0
    var intValue = -1
    var allSelected = true
    var selected = false
    while (i < numValues) {
      intValue = values(i)
      selected = field.filter.matches(valueLookUp(intValue).asInstanceOf[T])
      allSelected = allSelected & selected
      lookup.put(intValue, new SimpleBooleanProperty(selected))
      i += 1
    }
    setAllProperty(allSelected)
    lookup
  }

  private def setAllProperty(selected:Boolean) {
    allShouldChange = false
    allBooleanProperty.set(selected)
    allShouldChange = true
  }

  def flipValues(selectedValues:ObservableList[Int]) {
    if (selectedValues.size == 1 && selectedValues.get(0) == 0) {
      allBooleanProperty.set(!allBooleanProperty.get)
    } else {
      import scala.collection.JavaConversions._
      var allSelected = true
      selectedValues.foreach(intValue => {
        if (intValue != 0) {
          val booleanProperty = propertyLookUp(intValue)
          val newValue = !booleanProperty.get
          allSelected = allSelected & newValue
          booleanProperty.set(newValue)
        }
      })
      updateAllProperty(allSelected)
    }
  }

  def selectOneValue(intValue:Int) {
    if (intValue == 0) {
      allBooleanProperty.set(true)
    } else {
      updateAllProperty(false)
      propertyLookUp.foreach{case (value,property) => {
        property.set(intValue == value)
      }}
    }
  }

  def updateAllProperty(selected:Boolean) {
    if (selected) {
      val allSet = propertyLookUp.forall{case (_,property) => property.get}
      setAllProperty(allSet)
    } else {
      setAllProperty(false)
    }
  }

  def updateTableData() {
    val newField = field.withFilter(filter)
    val newTableData = tableData.getValue.replaceField(field, newField)
    tableData.setValue(newTableData)
  }

  def property(index:Int) = if (index == 0) allBooleanProperty else propertyLookUp(index)
  // TODO - All should be a binding that uses locale
  def text(index:Int) = if (index == 0) "All" else defaultRenderer.render(valueLookUp(index).asInstanceOf[T])

  def filter:Filter[T] = {
    if (allBooleanProperty.get) {
      NoFilter[T]()
    } else {
      val values = propertyLookUp.collect{case (intValue,property) if property.get => {
        valueLookUp(intValue.toInt).asInstanceOf[T]
      }}.toSet
      ContainsFilter[T](values)
    }
  }
}