package com.openaf.table.gui

import java.util.function.Predicate
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleBooleanProperty
import com.openaf.table.gui.binding.TableLocaleStringBinding
import com.openaf.table.lib.api._
import javafx.collections.ObservableList
import scala.collection.mutable
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.lang.{Boolean => JBoolean}

import scala.util.{Try, Success}

class FilterButtonNodeModel[T](val field:Field[T], tableFields:OpenAFTableFields) {
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
  private[gui] val values = tableFields.tableDataProperty.getValue.tableValues.fieldValues.values(field)
  private[gui] val numValues = values.length
  private val valueLookUp = tableFields.tableDataProperty.getValue.tableValues.valueLookUp(field.id).asInstanceOf[Array[T]]
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
      propertyLookUp.foreach{case (value,property) =>
        selected = field.filter.matches(valueLookUp(value.toInt))
        allSelected = allSelected & selected
        property.set(selected)
      }
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
  private var retainFilterType = field.filter.isInstanceOf[RetainFilter[_]] || field.filter.isInstanceOf[RejectAllFilter[_]]

  private[gui] def selectOneValue(intValue:Int) {
    if (intValue == 0) {
      allBooleanProperty.set(true)
    } else {
      updateAllProperty(false)
      propertyLookUp.foreach{case (value,property) => property.set(intValue == value)}
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

  private[gui] def updateTableState() {updateTableState(filter)}

  private[gui] def updateTableState(filter:Filter[T]) {
    val newField = field.withFilter(filter)
    val newTableState = tableFields.tableDataProperty.getValue.tableState.replaceField(field, newField)
    tableFields.requestTableStateProperty.setValue(RequestTableState(newTableState))
  }

  private[gui] def property(intValue:Int) = if (intValue == 0) allBooleanProperty else propertyLookUp(intValue)
  private def text(intValue:Int) = {
    if (intValue == 0) {
      TableLocaleStringBinding.stringFromBundle("all", tableFields.localeProperty.getValue)
    } else {
      renderer.render(value(intValue), tableFields.localeProperty.getValue)
    }
  }

  private def renderer = tableFields.renderers.renderer(field).asInstanceOf[Renderer[T]]
  private def ordering = tableFields.tableData.orderings.orderings(field.withDefaultFieldNodeState).asInstanceOf[Ordering[T]]
  private def orderingPredicateTry(textToParse:String, doesMatch:(T,Int)=>Boolean) = {
    if (textToParse.trim.isEmpty) {
      Try(AlwaysTruePredicate)
    } else {
      valueOption(textToParse).map(userValue => new AllAwarePredicate {
        override def matches(intValue:Int) = doesMatch(userValue, intValue)
      })
    }
  }
  private def valueOption(userText:String) = renderer.parser.safeParse(userText.trim)

  private val GreaterThanAndEqual = """\>=(.*)""".r
  private val GreaterThan = """\>(.*)""".r
  private val LessThanAndEqual = """\<=(.*)""".r
  private val LessThan = """\<(.*)""".r

  private[gui] def generatePredicate(userText:String) = {
    val predicateTry = userText match {
      case GreaterThanAndEqual(textToParse) => orderingPredicateTry(textToParse, (userValue,intValue) => ordering.gteq(value(intValue), userValue))
      case GreaterThan(textToParse) => orderingPredicateTry(textToParse, (userValue,intValue) => ordering.gt(value(intValue), userValue))
      case LessThanAndEqual(textToParse) => orderingPredicateTry(textToParse, (userValue,intValue) => ordering.lteq(value(intValue), userValue))
      case LessThan(textToParse) => orderingPredicateTry(textToParse, (userValue,intValue) => ordering.lt(value(intValue), userValue))
      case _ => Try(new TextMatchingPredicate(userText, text))
    }
    predicateTry.getOrElse(new TextMatchingPredicate(userText, text))
  }

  private[gui] def updateTableStateFromText(userText:String) = {
    val filterTry:Try[Filter[T]] = userText match {
      case GreaterThanAndEqual(textToParse) => valueOption(textToParse).map(value => OrderedFilter(value, ordering, greaterThan = true, andEqual = true))
      case GreaterThan(textToParse) => valueOption(textToParse).map(value => OrderedFilter(value, ordering, greaterThan = true, andEqual = false))
      case LessThanAndEqual(textToParse) => valueOption(textToParse).map(value => OrderedFilter(value, ordering, greaterThan = false, andEqual = true))
      case LessThan(textToParse) => valueOption(textToParse).map(value => OrderedFilter(value, ordering, greaterThan = false, andEqual = false))
      case _ =>
        println(s"TODO - Generate filter based on current text ($userText)") // TODO - actually do this
        Try(RetainAllFilter())
    }
    filterTry.foreach(filter => updateTableState(filter))
  }

  private[gui] def stringProperty(intValue:Int) = {
    new StringBinding {
      bind(tableFields.localeProperty)
      override def computeValue = text(intValue)
    }
  }
  private[gui] def value(intValue:Int) = valueLookUp(intValue)

  private[gui] def filter:Filter[T] = {
    if (allBooleanProperty.get) {
      RetainAllFilter[T]()
    } else {
      val filteredValues = propertyLookUp.collect{case (intValue,property) if property.get == retainFilterType =>
        valueLookUp(intValue.toInt)
      }.toSet
      if ((retainFilterType && filteredValues.isEmpty) || (!retainFilterType && (filteredValues.size == values.length))) {
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

trait AllAwarePredicate extends Predicate[Int] {
  def matches(intValue:Int):Boolean
  override def test(intValue:Int) = if (intValue == FilterButtonNodeModel.AllValue) true else matches(intValue)
}

object AlwaysTruePredicate extends Predicate[Int] {
  override def test(intValue:Int) = true
}

class TextMatchingPredicate(userText:String, text:Int=>String) extends AllAwarePredicate {
  override def matches(intValue:Int) = text(intValue).toLowerCase.contains(userText.toLowerCase)
}