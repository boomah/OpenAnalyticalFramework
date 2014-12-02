package com.openaf.table.server.datasources

import com.openaf.table.lib.api._
import com.openaf.table.lib.api.StandardFields._
import com.openaf.table.lib.api.TableValues._
import com.openaf.table.server.{FieldDefinition, FieldDefinitionGroups, NullFieldDefinition}
import java.util.{HashMap => JMap}
import scala.collection.mutable

case class RawRowBasedTableDataSource(data:Array[Array[Any]], fieldIDs:Array[FieldID],
                                      fieldDefinitionGroups:FieldDefinitionGroups) extends TableDataSource {
  def result(tableState:TableState) = RawRowBasedTableDataSource.result(tableState, data, fieldIDs, fieldDefinitionGroups)
}

/**
 * A Java style (while loops) TableDataSource used for testing and also as a benchmark for best case single threaded
 * performance.
 */
object RawRowBasedTableDataSource {
  // This is far from idiomatic Scala. Written this way for speed.
  def result(tableState:TableState, data:Array[Array[Any]], fieldIDs:Array[FieldID],
             fieldDefinitionGroups:FieldDefinitionGroups) = {
    val fieldDefinitionGroup = fieldDefinitionGroups.rootGroup
    val allFieldIDs = tableState.distinctFieldIDs
    val fieldIDToLookUp:Map[FieldID,JMap[Any,WrappedInt]] = allFieldIDs.map(fieldID => {
      val map = new JMap[Any,WrappedInt]
      map.put(fieldID, new WrappedInt(0))
      fieldID -> map
    })(collection.breakOut)
    val fieldsValueCounter = new Array[Int](allFieldIDs.size)

    val filterFields = tableState.filterFields.toArray
    val filterFieldIDs = tableState.tableLayout.filterFieldIDs
    val filterFieldDefinitions = filterFieldIDs.map(id => fieldDefinitionGroup.fieldDefinition(id)).toArray
    val numFilterCols = filterFieldIDs.length
    var filterCounter = 0
    val filterFieldPositions = filterFieldIDs.map(fieldIDs.indexOf(_)).toArray
    val filtersLookUp = filterFieldIDs.map(fieldIDToLookUp).toArray
    val filtersValueCounter = filterFieldIDs.map(allFieldIDs.indexOf(_)).toArray

    val rowHeaderFields = tableState.rowHeaderFields.toArray
    val rowHeaderFieldIDs = tableState.tableLayout.rowHeaderFieldIDs
    val rowHeaderFieldDefinitions = rowHeaderFieldIDs.map(id => fieldDefinitionGroup.fieldDefinition(id)).toArray
    val numRowHeaderCols = rowHeaderFieldIDs.length
    var rowHeaderCounter = 0
    val rowHeaderFieldPositions = rowHeaderFieldIDs.map(fieldIDs.indexOf(_)).toArray
    val rowHeaders = new mutable.HashSet[RowHeaderPath]
    val rowHeadersLookUp = rowHeaderFieldIDs.map(fieldIDToLookUp).toArray
    val rowHeadersValueCounter = rowHeaderFieldIDs.map(allFieldIDs.indexOf(_)).toArray

    val columnHeaderFieldPaths = tableState.tableLayout.columnHeaderLayout.paths
    val numPaths = columnHeaderFieldPaths.length
    var pathsCounter = 0
    val numColumnHeaderColsPerPath = columnHeaderFieldPaths.map(_.fields.length).toArray
    var numColumnHeaderCols = -1
    var colHeaderColCounter = 0
    val colHeaderFieldsPositions = columnHeaderFieldPaths.map(_.fields.map(field => fieldIDs.indexOf(field.id)).toArray).toArray
    val columnHeaderPaths = new mutable.HashSet[ColumnHeaderPath]
    val colHeadersLookUps = columnHeaderFieldPaths.map(path =>
      path.fields.map(field => fieldIDToLookUp(field.id)).toArray
    ).toArray
    val colHeadersValuesCounter = columnHeaderFieldPaths.map(path =>
      path.fields.map(field => allFieldIDs.indexOf(field.id)).toArray
    ).toArray

    val columnHeaderMeasureFieldPositions = columnHeaderFieldPaths.map(_.measureFieldIndex).toArray
    val columnHeaderPathsMeasureOptions = columnHeaderFieldPaths.map(_.measureFieldOption)
    val countFieldPosition = -2
    val measureFieldPositions = columnHeaderPathsMeasureOptions.map{
      case Some(field) => {
        val index = fieldIDs.indexOf(field.id)
        if (index >= 0) {
          index
        } else {
          field.id match {
            case CountField.id => countFieldPosition
            case _ => -1
          }
        }
      }
      case _ => -1
    }
    val measureFieldDefinitions = columnHeaderPathsMeasureOptions.map{
      case Some(field) => fieldDefinitionGroup.fieldDefinition(field.id)
      case _ => NullFieldDefinition
    }
    val columnHeaderPathsFields = columnHeaderFieldPaths.map(_.fields.toArray).toArray
    val columnHeaderPathsFieldDefinitions = columnHeaderPathsFields.map(_.map(field => {
      fieldDefinitionGroup.fieldDefinition(field.id)
    }))

    val aggregatedData = new mutable.AnyRefMap[DataPath,Any]

    val fieldValuesBitSets:Map[Field[_],mutable.BitSet] = tableState.allFields.map(_ -> new mutable.BitSet).toMap

    val dataLength = data.length
    var dataCounter = 0

    var dataRow:Array[Any] = null
    var rowHeaderValues:Array[Int] = null
    var value:Any = -1
    var newDataValue:Any = null
    var lookUp:JMap[Any,WrappedInt] = null
    var intForValue:WrappedInt = null
    var fieldsValueCounterIndex = -1
    var measureFieldIndex = -1
    var columnHeaderFieldPositions:Array[Int] = null
    var columnHeaderLookUp:Array[JMap[Any,WrappedInt]] = null
    var columnHeaderValueCounter:Array[Int] = null
    var colHeaderValues:Array[Int] = null
    var measureFieldPosition = -1
    var key:DataPath = null
    var matchesFilter = true
    var columnHeaderFields:Array[Field[_]] = null
    var columnHeaderFieldDefinitions:Array[FieldDefinition] = null
    var columnHeaderPath:ColumnHeaderPath = null

    val rowTotals = new mutable.ArrayBuffer[RowHeaderPath](numRowHeaderCols * 2)
    var rowTotalsCounter = 0
    var numRowTotals = 0

    val rowHeaderCollapsedStates = rowHeaderFields.zipWithIndex.map{case (field,fieldIndex) => {
      new RawTableDataSourceCollapsedState(field, fieldIndex, rowHeadersLookUp, rowHeadersValueCounter, fieldsValueCounter)
    }}
    var rowHeaderCollapsed = false

    while (dataCounter < dataLength) {
      dataRow = data(dataCounter)
      matchesFilter = true
      rowHeaderCollapsed = false

      while (matchesFilter && filterCounter < numFilterCols) {
        value = dataRow(filterFieldPositions(filterCounter))
        val fieldDefinition = filterFieldDefinitions(filterCounter)
        val field = filterFields(filterCounter).asInstanceOf[Field[fieldDefinition.V]]
        matchesFilter = field.filter.matches(value.asInstanceOf[fieldDefinition.V])
        lookUp = filtersLookUp(filterCounter)
        intForValue = lookUp.get(value)
        if (intForValue == null) {
          fieldsValueCounterIndex = filtersValueCounter(filterCounter)
          intForValue = new WrappedInt(fieldsValueCounter(fieldsValueCounterIndex) + 1)
          fieldsValueCounter(fieldsValueCounterIndex) = intForValue.int
          lookUp.put(value, intForValue)
          fieldValuesBitSets(field) += intForValue.int
        } else {
          fieldValuesBitSets(field) += intForValue.int
        }
        filterCounter += 1
      }
      filterCounter = 0

      rowHeaderValues = new Array[Int](numRowHeaderCols)
      rowTotals.clear()
      while (matchesFilter && rowHeaderCounter < numRowHeaderCols) {
        value = dataRow(rowHeaderFieldPositions(rowHeaderCounter))
        val fieldDefinition = rowHeaderFieldDefinitions(rowHeaderCounter)
        val field = rowHeaderFields(rowHeaderCounter).asInstanceOf[Field[fieldDefinition.V]]
        matchesFilter = field.filter.matches(value.asInstanceOf[fieldDefinition.V])
        lookUp = rowHeadersLookUp(rowHeaderCounter)
        intForValue = lookUp.get(value)
        if (intForValue == null) {
          fieldsValueCounterIndex = rowHeadersValueCounter(rowHeaderCounter)
          intForValue = new WrappedInt(fieldsValueCounter(fieldsValueCounterIndex) + 1)
          fieldsValueCounter(fieldsValueCounterIndex) = intForValue.int
          lookUp.put(value, intForValue)
          rowHeaderValues(rowHeaderCounter) = intForValue.int
          fieldValuesBitSets(field) += intForValue.int
        } else {
          rowHeaderValues(rowHeaderCounter) = intForValue.int
          fieldValuesBitSets(field) += intForValue.int
        }

        // Don't add totals for the last row header field
        if (rowHeaderCounter < (numRowHeaderCols - 1)) {
          if (rowHeaderCollapsedStates(rowHeaderCounter).collapsed(rowHeaderValues)) {
            rowHeaderCollapsed = true
            rowTotals += new RowHeaderPath(generateTotalArray(rowHeaderValues, rowHeaderCounter, TotalTopInt))
          } else {
            if (field.totals.top) {
              rowTotals += new RowHeaderPath(generateTotalArray(rowHeaderValues, rowHeaderCounter, TotalTopInt))
            }
            if (field.totals.bottom) {
              rowTotals += new RowHeaderPath(generateTotalArray(rowHeaderValues, rowHeaderCounter, TotalBottomInt))
            }
          }
        }
        rowHeaderCounter += 1
      }
      rowHeaderCounter = 0
      if (matchesFilter && !rowHeaderCollapsed) {
        rowHeaders += new RowHeaderPath(rowHeaderValues)
        rowHeaders ++= rowTotals
      } else if (matchesFilter && rowHeaderCollapsed) {
        rowHeaders ++= rowTotals
      }

      while (matchesFilter && pathsCounter < numPaths) {
        measureFieldIndex = columnHeaderMeasureFieldPositions(pathsCounter)
        columnHeaderFieldPositions = colHeaderFieldsPositions(pathsCounter)
        columnHeaderLookUp = colHeadersLookUps(pathsCounter)
        columnHeaderValueCounter = colHeadersValuesCounter(pathsCounter)
        numColumnHeaderCols = numColumnHeaderColsPerPath(pathsCounter)
        colHeaderValues = new Array[Int](numColumnHeaderCols)
        columnHeaderFields = columnHeaderPathsFields(pathsCounter)
        columnHeaderFieldDefinitions = columnHeaderPathsFieldDefinitions(pathsCounter)
        while (matchesFilter && colHeaderColCounter < numColumnHeaderCols) {
          if (colHeaderColCounter != measureFieldIndex) {
            value = dataRow(columnHeaderFieldPositions(colHeaderColCounter))
            val fieldDefinition = columnHeaderFieldDefinitions(colHeaderColCounter)
            val field = columnHeaderFields(colHeaderColCounter).asInstanceOf[Field[fieldDefinition.V]]
            matchesFilter = field.filter.matches(value.asInstanceOf[fieldDefinition.V])
            lookUp = columnHeaderLookUp(colHeaderColCounter)
            intForValue = lookUp.get(value)
            if (intForValue == null) {
              fieldsValueCounterIndex = columnHeaderValueCounter(colHeaderColCounter)
              intForValue = new WrappedInt(fieldsValueCounter(fieldsValueCounterIndex) + 1)
              fieldsValueCounter(fieldsValueCounterIndex) = intForValue.int
              lookUp.put(value, intForValue)
              colHeaderValues(colHeaderColCounter) = intForValue.int
              fieldValuesBitSets(field) += intForValue.int
            } else {
              colHeaderValues(colHeaderColCounter) = intForValue.int
              fieldValuesBitSets(field) += intForValue.int
            }
          }
          colHeaderColCounter += 1
        }
        colHeaderColCounter = 0
        if (matchesFilter) {
          columnHeaderPath = new ColumnHeaderPath(pathsCounter, colHeaderValues)
          columnHeaderPaths += columnHeaderPath

          measureFieldPosition = measureFieldPositions(pathsCounter)
          // If there isn't a measure field there is no need to update the aggregated data
          if (measureFieldPosition != -1) {
            if (measureFieldPosition >= 0) {
              value = dataRow(measureFieldPosition)
            } else if (measureFieldPosition == countFieldPosition) {
              // Count field
              value = 1
            }

            val fieldDefinition = measureFieldDefinitions(pathsCounter)
            if (!rowHeaderCollapsed) {
              key = new DataPath(rowHeaderValues, columnHeaderPath)
              if (aggregatedData.contains(key)) {
                newDataValue = fieldDefinition.combiner.combine(
                  aggregatedData(key).asInstanceOf[fieldDefinition.C],
                  value.asInstanceOf[fieldDefinition.V]
                )
              } else {
                newDataValue = fieldDefinition.combiner.combine(
                  fieldDefinition.combiner.initialCombinedValue,
                  value.asInstanceOf[fieldDefinition.V]
                )
              }
              aggregatedData.update(key, newDataValue)
            }

            rowTotalsCounter = 0
            numRowTotals = rowTotals.size
            while (rowTotalsCounter < numRowTotals) {
              key = new DataPath(rowTotals(rowTotalsCounter).values, columnHeaderPath)
              if (aggregatedData.contains(key)) {
                newDataValue = fieldDefinition.combiner.combine(
                  aggregatedData(key).asInstanceOf[fieldDefinition.C],
                  value.asInstanceOf[fieldDefinition.V]
                )
              } else {
                newDataValue = fieldDefinition.combiner.combine(
                  fieldDefinition.combiner.initialCombinedValue,
                  value.asInstanceOf[fieldDefinition.V]
                )
              }
              aggregatedData.update(key, newDataValue)
              rowTotalsCounter += 1
            }
          }
        }
        matchesFilter = true
        pathsCounter += 1
      }
      pathsCounter = 0
      dataCounter += 1
    }

    val valueLookUp = fieldIDToLookUp.map{case (fieldID,hashMap) => {
      val values = new Array[Any](hashMap.size)
      val keyIterator = hashMap.keySet.iterator
      var key:Any = null
      while (keyIterator.hasNext) {
        key = keyIterator.next
        values(hashMap.get(key).int) = key
      }
      fieldID -> values
    }}

    // If there are no row fields or measure fields there with be an empty row in the headers that isn't needed so
    // remove it
    val rowHeadersToUse = if (rowHeaderFieldIDs.nonEmpty || columnHeaderPathsMeasureOptions.exists(_.isDefined)) {
      rowHeaders.map(_.values).toArray
    } else {
      Array.empty[Array[Int]]
    }

    val fieldValues = FieldValues(fieldValuesBitSets.map{case (field,bitSet) => field -> bitSet.toArray}.toMap)
    val resultDetails = ResultState(
      FilterState(isFiltered = true),
      TotalsState(totalsAdded = true),
      SortState.NoSorting
    )
    Result(rowHeadersToUse, columnHeaderPaths.toArray, aggregatedData.toMap, fieldValues, valueLookUp, resultDetails)
  }

  @inline private def generateTotalArray(array:Array[Int], upTo:Int, totalInt:Int) = {
    val totalsArray = new Array[Int](array.length)
    var totalsCounter = 0
    while (totalsCounter <= upTo) {
      totalsArray(totalsCounter) = array(totalsCounter)
      totalsCounter += 1
    }
    while (totalsCounter < totalsArray.length) {
      totalsArray(totalsCounter) = totalInt
      totalsCounter += 1
    }
    totalsArray
  }
}

private class WrappedInt(val int:Int)