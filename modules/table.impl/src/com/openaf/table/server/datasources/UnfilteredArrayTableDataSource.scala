package com.openaf.table.server.datasources

import com.openaf.table.lib.api._
import com.openaf.table.lib.api.StandardFields._
import com.openaf.table.lib.api.TableValues._
import com.openaf.table.server._
import java.util.{HashMap => JMap}
import scala.collection.mutable

/**
 * TableDataSource that generates field values and totals as well as applying filters and sorting.
 *
 * Implementations provide the data as a 2D array for performance reasons. This array should not be updated whilst it is
 * in use.
 *
 * Implementation detail: The TableData generation is done in a Java style (while loops) for performance reasons. As
 * such this TableDataSource can be used as a benchmark for best case single threaded performance.
 */
trait UnfilteredArrayTableDataSource extends TableDataSource {
  /**
   * The raw data required to generate the TableData given the tableState supplied. The DataSourceTable should contain
   * data and FieldDefinitions for any FieldID used in the tableState supplied.
   */
  def dataSourceTable(tableState:TableState):DataSourceTable

  final def tableData(tableStateNoKeys:TableState) = {
    val tableState = tableStateNoKeys.generateFieldKeys
    val generatedPivotData = pivotData(tableState)
    TableDataGenerator.tableData(generatedPivotData)
  }

  def pivotData(tableState:TableState) = {
    val dataSourceTableProvided = dataSourceTable(tableState)
    val allFieldIDs = tableState.distinctFieldIDs
    val fieldDefinitionGroup = dataSourceTableProvided.fieldDefinitionGroups.rootGroup

    {
      val allFieldIDsSet = allFieldIDs.toSet
      val fieldIDsProvidedByFieldDefinitions = fieldDefinitionGroup.fieldDefinitions.map(_.fieldID).toSet
      val missingFieldDefinitionFieldIDs = allFieldIDsSet -- fieldIDsProvidedByFieldDefinitions
      require(missingFieldDefinitionFieldIDs.isEmpty, "There must be a FieldDefinition for all FieldIDs used in the " +
        s"tableState. There are missing FieldIDs for $missingFieldDefinitionFieldIDs")
      val missingDataFieldIDs = allFieldIDsSet -- (dataSourceTableProvided.fieldIDs.toSet ++ AllFieldIDs.toSet)
      require(missingDataFieldIDs.isEmpty, "There must be a data column for all FieldIDs used in the tableState. " +
        s"There are missing FieldIDs for $missingDataFieldIDs")
    }

    val fieldIDs = dataSourceTableProvided.fieldIDs
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
    val rowHeaders = new IntArraySet
    val rowHeadersLookUp = rowHeaderFieldIDs.map(fieldIDToLookUp).toArray
    val rowHeadersValueCounter = rowHeaderFieldIDs.map(allFieldIDs.indexOf(_)).toArray

    val columnHeaderFieldPaths = tableState.tableLayout.columnHeaderLayout.paths
    val numPaths = columnHeaderFieldPaths.length
    var pathsCounter = 0
    val numColumnHeaderRowsPerPath = columnHeaderFieldPaths.map(_.fields.length).toArray
    var numColumnHeaderRows = -1
    var colHeaderRowCounter = 0
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
    val filterFieldsValuesBitSets = filterFields.map(field => fieldValuesBitSets(field))
    val rowHeaderFieldsValuesBitSets = rowHeaderFields.map(field => fieldValuesBitSets(field))
    val columnHeaderFields = tableState.columnHeaderLayout.allFields
    val columnHeaderFieldsValuesBitSets = new Array[mutable.BitSet](columnHeaderFields.size)
    columnHeaderFields.foreach(field => columnHeaderFieldsValuesBitSets(field.key.number) = fieldValuesBitSets(field))

    val data = dataSourceTableProvided.data
    val dataLength = data.length
    var dataCounter = 0

    var dataRow:Array[Any] = null
    var rowHeaderValues:Array[Int] = null
    var value:Any = -1
    var lookUp:JMap[Any,WrappedInt] = null
    var intForValue:WrappedInt = null
    var fieldsValueCounterIndex = -1
    var measureFieldIndex = -1
    var columnHeaderFieldPositions:Array[Int] = null
    var columnHeaderLookUp:Array[JMap[Any,WrappedInt]] = null
    var columnHeaderValueCounter:Array[Int] = null
    var colHeaderValues:Array[Int] = null
    var measureFieldPosition = -1
    var matchesFilter = true
    var columnHeaderFieldsForPath:Array[Field[_]] = null
    var columnHeaderFieldDefinitions:Array[FieldDefinition] = null
    var columnHeaderPath:ColumnHeaderPath = null

    val rowTotals = new mutable.ArrayBuffer[Array[Int]](numRowHeaderCols * 2)
    var rowTotalsCounter = 0
    var numRowTotals = 0

    val maxColumnPathLength = if (numColumnHeaderRowsPerPath.isEmpty) 0 else numColumnHeaderRowsPerPath.max
    val columnTotals = new mutable.ArrayBuffer[ColumnHeaderPath](maxColumnPathLength * 2)
    var columnTotalsCounter = 0
    var numColumnTotals = 0

    val rowHeaderCollapsedStates = rowHeaderFields.zipWithIndex.map{case (field,fieldIndex) =>
      new CollapsedStateHelper(rowHeaderFields, fieldIndex, rowHeadersLookUp, rowHeadersValueCounter, fieldsValueCounter)
    }
    var rowHeaderCollapsed = false
    val columnHeaderCollapsedStates = new Array[CollapsedStateHelper](columnHeaderFields.size)
    columnHeaderPathsFields.zipWithIndex.foreach{case (fields, pathIndex) => fields.zipWithIndex.foreach{case (field, fieldIndex) =>
      columnHeaderCollapsedStates(field.key.number) = new CollapsedStateHelper(fields, fieldIndex,
        colHeadersLookUps(pathIndex), colHeadersValuesCounter(pathIndex), fieldsValueCounter)
    }}
    var columnHeaderCollapsed = false

    while (dataCounter < dataLength) {
      dataRow = data(dataCounter)
      matchesFilter = true
      rowHeaderCollapsed = false
      columnHeaderCollapsed = false

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
          filterFieldsValuesBitSets(filterCounter) += intForValue.int
        } else {
          filterFieldsValuesBitSets(filterCounter) += intForValue.int
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
          rowHeaderFieldsValuesBitSets(rowHeaderCounter) += intForValue.int
        } else {
          rowHeaderValues(rowHeaderCounter) = intForValue.int
          rowHeaderFieldsValuesBitSets(rowHeaderCounter) += intForValue.int
        }

        // Don't add totals for the last row header field
        if (!rowHeaderCollapsed && rowHeaderCounter < (numRowHeaderCols - 1)) {
          if (rowHeaderCollapsedStates(rowHeaderCounter).collapsed(rowHeaderValues)) {
            rowHeaderCollapsed = true
            rowTotals += generateTotalArray(rowHeaderValues, rowHeaderCounter, TotalTopInt)
          } else {
            if (field.totals.top) {
              rowTotals += generateTotalArray(rowHeaderValues, rowHeaderCounter, TotalTopInt)
            }
            if (field.totals.bottom) {
              rowTotals += generateTotalArray(rowHeaderValues, rowHeaderCounter, TotalBottomInt)
            }
          }
        }
        rowHeaderCounter += 1
      }
      rowHeaderCounter = 0
      if (matchesFilter) {
        if (!rowHeaderCollapsed) {
          rowHeaders += rowHeaderValues
        }
        numRowTotals = rowTotals.length
        rowTotalsCounter = 0
        while (rowTotalsCounter < numRowTotals) {
          rowHeaders += rowTotals(rowTotalsCounter)
          rowTotalsCounter += 1
        }
      }

      while (matchesFilter && pathsCounter < numPaths) {
        columnTotals.clear()
        measureFieldIndex = columnHeaderMeasureFieldPositions(pathsCounter)
        columnHeaderFieldPositions = colHeaderFieldsPositions(pathsCounter)
        columnHeaderLookUp = colHeadersLookUps(pathsCounter)
        columnHeaderValueCounter = colHeadersValuesCounter(pathsCounter)
        numColumnHeaderRows = numColumnHeaderRowsPerPath(pathsCounter)
        colHeaderValues = new Array[Int](numColumnHeaderRows)
        columnHeaderFieldsForPath = columnHeaderPathsFields(pathsCounter)
        columnHeaderFieldDefinitions = columnHeaderPathsFieldDefinitions(pathsCounter)
        while (matchesFilter && colHeaderRowCounter < numColumnHeaderRows) {
          val fieldDefinition = columnHeaderFieldDefinitions(colHeaderRowCounter)
          val field = columnHeaderFieldsForPath(colHeaderRowCounter).asInstanceOf[Field[fieldDefinition.V]]
          if (colHeaderRowCounter != measureFieldIndex) {
            value = dataRow(columnHeaderFieldPositions(colHeaderRowCounter))
            matchesFilter = field.filter.matches(value.asInstanceOf[fieldDefinition.V])
            lookUp = columnHeaderLookUp(colHeaderRowCounter)
            intForValue = lookUp.get(value)
            if (intForValue == null) {
              fieldsValueCounterIndex = columnHeaderValueCounter(colHeaderRowCounter)
              intForValue = new WrappedInt(fieldsValueCounter(fieldsValueCounterIndex) + 1)
              fieldsValueCounter(fieldsValueCounterIndex) = intForValue.int
              lookUp.put(value, intForValue)
              colHeaderValues(colHeaderRowCounter) = intForValue.int
              columnHeaderFieldsValuesBitSets(field.key.number) += intForValue.int
            } else {
              colHeaderValues(colHeaderRowCounter) = intForValue.int
              columnHeaderFieldsValuesBitSets(field.key.number) += intForValue.int
            }
          }

          // Don't add totals for the last column header field
          if (!columnHeaderCollapsed && colHeaderRowCounter < (numColumnHeaderRows - 1)) {
            if (columnHeaderCollapsedStates(field.key.number).collapsed(colHeaderValues)) {
              columnHeaderCollapsed = true
              columnTotals += new ColumnHeaderPath(pathsCounter, generateTotalArray(colHeaderValues, colHeaderRowCounter, TotalTopInt))
            } else {
              if (field.totals.top) {
                columnTotals += new ColumnHeaderPath(pathsCounter, generateTotalArray(colHeaderValues, colHeaderRowCounter, TotalTopInt))
              }
              if (field.totals.bottom) {
                columnTotals += new ColumnHeaderPath(pathsCounter, generateTotalArray(colHeaderValues, colHeaderRowCounter, TotalBottomInt))
              }
            }
          }

          colHeaderRowCounter += 1
        }
        colHeaderRowCounter = 0
        if (matchesFilter) {
          columnHeaderPath = new ColumnHeaderPath(pathsCounter, colHeaderValues)
          if (!columnHeaderCollapsed) {
            columnHeaderPaths += columnHeaderPath
          }

          numColumnTotals = columnTotals.length
          columnTotalsCounter = 0
          while (columnTotalsCounter < numColumnTotals) {
            columnHeaderPaths += columnTotals(columnTotalsCounter)
            columnTotalsCounter += 1
          }

          measureFieldPosition = measureFieldPositions(pathsCounter)
          // If there isn't a measure field there is no need to update the aggregated data
          if (measureFieldPosition != -1) {
            if (measureFieldPosition >= 0) {
              value = dataRow(measureFieldPosition)
            } else if (measureFieldPosition == countFieldPosition) {
              // Count field
              value = MutIntCombiner.One
            }

            val fieldDefinition = measureFieldDefinitions(pathsCounter)
            if (!rowHeaderCollapsed && !columnHeaderCollapsed) {
              combine(value, fieldDefinition, rowHeaderValues, columnHeaderPath, aggregatedData)
            }

            rowTotalsCounter = 0
            while (rowTotalsCounter < numRowTotals) {
              combine(value, fieldDefinition, rowTotals(rowTotalsCounter), columnHeaderPath, aggregatedData)
              rowTotalsCounter += 1
            }

            columnTotalsCounter = 0
            while (columnTotalsCounter < numColumnTotals) {
              combine(value, fieldDefinition, rowHeaderValues, columnTotals(columnTotalsCounter), aggregatedData)

              rowTotalsCounter = 0
              while (rowTotalsCounter < numRowTotals) {
                combine(value, fieldDefinition, rowTotals(rowTotalsCounter), columnTotals(columnTotalsCounter), aggregatedData)
                rowTotalsCounter += 1
              }

              columnTotalsCounter += 1
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
      rowHeaders.toArray
    } else {
      Array.empty[Array[Int]]
    }

    val fieldValues = FieldValues(fieldValuesBitSets.map{case (field,bitSet) => field -> bitSet.toArray}.toMap)
    PivotData(tableState, dataSourceTableProvided.fieldDefinitionGroups, rowHeadersToUse, columnHeaderPaths.toArray,
      aggregatedData.toMap, fieldValues, valueLookUp)
  }

  @inline private final def generateTotalArray(array:Array[Int], upTo:Int, totalInt:Int) = {
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

  @inline private final def combine(value:Any, fieldDefinition:FieldDefinition, rowHeaderValues:Array[Int],
                              columnHeaderPath:ColumnHeaderPath,  aggregatedData:mutable.AnyRefMap[DataPath,Any]) {
    val combiner = fieldDefinition.combiner
    val key = new DataPath(rowHeaderValues, columnHeaderPath)
    if (aggregatedData.contains(key)) {
      val newDataValue = combiner.combine(
        aggregatedData(key).asInstanceOf[fieldDefinition.C],
        value.asInstanceOf[fieldDefinition.V]
      )
      if (!combiner.isMutable) {aggregatedData.update(key, newDataValue)}
    } else {
      val newDataValue = combiner.combine(combiner.initialCombinedValue, value.asInstanceOf[fieldDefinition.V])
      aggregatedData.update(key, newDataValue)
    }
  }
}

private class WrappedInt(val int:Int)