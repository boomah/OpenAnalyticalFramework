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
    def changed(value:ObservableValue[_<:JBoolean], oldValue:JBoolean, newValue:JBoolean) {
      if (allShouldChange) {
        propertyLookUp.values.foreach(_.setValue(newValue))
        retainFilterType = !newValue
      }
    }
  })
  private[gui] val values = tableData.getValue.tableValues.fieldValues.values(field)
  private[gui] val numValues = values.length
  private val defaultRenderer = tableData.getValue.defaultRenderers(field.id).asInstanceOf[Renderer[T]]
  private val valueLookUp = tableData.getValue.tableValues.valueLookUp(field.id).asInstanceOf[Array[T]]
  private val propertyLookUp = {
    val lookup = new mutable.LongMap[SimpleBooleanProperty]
    var i = 0
    var intValue = -1
    var allSelected = true
    var selected = false
    while (i < numValues) {
      intValue = values(i)
      selected = field.filter.matches(valueLookUp(intValue))
      allSelected = allSelected & selected
      lookup.put(intValue, new SimpleBooleanProperty(selected))
      i += 1
    }
    setAllProperty(allSelected)
    lookup
  }

  private var resetRequired = false // We don't need to reset on the very first showing or if nothing has changed
  private[gui] def reset() {
    if (resetRequired) {
      resetRequired = false
      var allSelected = true
      var selected = false
      propertyLookUp.foreach{case (value,property) => {
        selected = field.filter.matches(valueLookUp(value.toInt))
        allSelected = allSelected & selected
        property.set(selected)
      }}
      setAllProperty(allSelected)
    }
  }

  private def setAllProperty(selected:Boolean) {
    allShouldChange = false
    allBooleanProperty.set(selected)
    allShouldChange = true
  }

  private[gui] def flipValues(selectedValues:ObservableList[Int]) {
    if (selectedValues.size == 1 && selectedValues.get(0) == 0) {
      allBooleanProperty.set(!allBooleanProperty.get)
    } else {
      import scala.collection.JavaConversions._
      var specifiedValuesAllSelected = true
      selectedValues.foreach(intValue => {
        if (intValue != 0) {
          val booleanProperty = propertyLookUp(intValue)
          val newValue = !booleanProperty.get
          specifiedValuesAllSelected = specifiedValuesAllSelected & newValue
          booleanProperty.set(newValue)
        }
      })
      updateAllProperty(specifiedValuesAllSelected)
    }
  }

  // Determines whether we want the filter to be retaining or rejecting. Updated based on what a user clicks on. e.g. if
  // a user selects just one value then we are in retain mode. If a user deselects one value when everything is selected
  // then we are in reject mode.
  private var retainFilterType = field.filter.isInstanceOf[RejectAllFilter[_]]

  private[gui] def selectOneValue(intValue:Int) {
    if (intValue == 0) {
      allBooleanProperty.set(true)
    } else {
      updateAllProperty(false)
      propertyLookUp.foreach{case (value,property) => {
        property.set(intValue == value)
      }}
      retainFilterType = true
    }
  }

  private[gui] def updateAllProperty(selected:Boolean) {
    if (selected) {
      val allSet = propertyLookUp.forall{case (_,property) => property.get}
      setAllProperty(allSet)
    } else {
      setAllProperty(false)
    }
    resetRequired = true
  }

  private[gui] def updateTableData() {updateTableData(filter)}

  private[gui] def updateTableData(filter:Filter[T]) {
    val newField = field.withFilter(filter)
    val newTableData = tableData.getValue.replaceField(field, newField)
    tableData.setValue(newTableData)
  }

  private[gui] def property(intValue:Int) = if (intValue == 0) allBooleanProperty else propertyLookUp(intValue)
  // TODO - All should be a binding that uses locale
  private[gui] def text(intValue:Int) = if (intValue == 0) "All" else defaultRenderer.render(value(intValue))
  private[gui] def value(intValue:Int) = valueLookUp(intValue)

  private[gui] def filter:Filter[T] = {
    if (allBooleanProperty.get) {
      RetainAllFilter[T]()
    } else {
      val filteredValues = propertyLookUp.collect{case (intValue,property) if property.get == retainFilterType => {
        valueLookUp(intValue.toInt)
      }}.toSet
      if ((retainFilterType && filteredValues.isEmpty) || (!retainFilterType && (filteredValues.size == values.size))) {
        RejectAllFilter[T]()
      } else {
        if (retainFilterType) {
          RetainFilter[T](filteredValues)
        } else {
          RejectFilter[T](filteredValues)
        }
      }
    }
  }
}

object FilterButtonNodeModel {
  val AllValue = 0
}