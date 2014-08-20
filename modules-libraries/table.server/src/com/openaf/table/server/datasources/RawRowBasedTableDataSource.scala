package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{FieldValues, Field, FieldID, TableState}
import com.openaf.table.lib.api.StandardFields._
import com.openaf.table.server.{FieldDefinition, FieldDefinitionGroups, NullFieldDefinition}
import java.util.{HashMap => JMap}
import scala.collection.{mutable, JavaConversions}

case class RawRowBasedTableDataSource(data:Array[Array[Any]], fieldIDs:Array[FieldID],
                                      fieldDefinitionGroups:FieldDefinitionGroups) extends TableDataSource {
  def result(tableState:TableState) = RawRowBasedTableDataSource.result(tableState, data, fieldIDs, fieldDefinitionGroups)
}

object RawRowBasedTableDataSource {
  // This is far from idiomatic Scala. Written this way for speed.
  def result(tableState:TableState, data:Array[Array[Any]], fieldIDs:Array[FieldID],
             fieldDefinitionGroups:FieldDefinitionGroups) = {
    val fieldDefinitionGroup = fieldDefinitionGroups.rootGroup
    val allFieldIDs = tableState.distinctFieldIDs
    val fieldIDToLookUp:Map[FieldID,JMap[Any,Int]] = allFieldIDs.map(fieldID => {
      val map = new JMap[Any,Int]
      map.put(fieldID, 0)
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
    val rowHeaders = new mutable.HashSet[IntArrayWrapper]
    val rowHeadersLookUp = rowHeaderFieldIDs.map(fieldIDToLookUp).toArray
    val rowHeadersValueCounter = rowHeaderFieldIDs.map(allFieldIDs.indexOf(_)).toArray

    val columnHeaderPaths = tableState.tableLayout.columnHeaderLayout.paths
    val numPaths = columnHeaderPaths.length
    var pathsCounter = 0
    val numColumnHeaderColsPerPath = columnHeaderPaths.map(_.fields.length).toArray
    var numColumnHeaderCols = -1
    var colHeaderColCounter = 0
    val colHeaderFieldsPositions = columnHeaderPaths.map(_.fields.map(field => fieldIDs.indexOf(field.id)).toArray).toArray
    val colHeaders = Array.fill(numPaths)(new mutable.HashSet[IntArrayWrapper])
    val colHeadersLookUps = columnHeaderPaths.map(path =>
      path.fields.map(field => fieldIDToLookUp(field.id)).toArray
    ).toArray
    val colHeadersValuesCounter = columnHeaderPaths.map(path =>
      path.fields.map(field => allFieldIDs.indexOf(field.id)).toArray
    ).toArray

    val columnHeaderMeasureFieldPositions = columnHeaderPaths.map(_.measureFieldIndex).toArray
    val columnHeaderPathsMeasureOptions = columnHeaderPaths.map(_.measureFieldOption).toArray
    val measureFieldPositions = columnHeaderPathsMeasureOptions.map{
      case Some(field) => {
        val index = fieldIDs.indexOf(field.id)
        if (index >= 0) {
          index
        } else {
          field.id match {
            case CountField.id => -2
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
    val columnHeaderPathsFields = columnHeaderPaths.map(_.fields.toArray).toArray
    val columnHeaderPathsFieldDefinitions = columnHeaderPathsFields.map(_.map(field => {
      fieldDefinitionGroup.fieldDefinition(field.id)
    }))

    val aggregatedDataForPath = Array.fill(numPaths)(new JMap[IntArrayWrapperKey,Any])

    val fieldValuesBitSets:Map[Field[_],mutable.BitSet] = tableState.allFields.map(_ -> new mutable.BitSet).toMap

    val dataLength = data.length
    var dataCounter = 0

    var dataRow:Array[Any] = null
    var rowHeaderValues:Array[Int] = null
    var value:Any = -1
    var newDataValue:Any = null
    var lookUp:JMap[Any,Int] = null
    var intForValue = -1
    var fieldsValueCounterIndex = -1
    var newCounter = -1
    var measureFieldIndex = -1
    var columnHeaderFieldPositions:Array[Int] = null
    var columnHeaderLookUp:Array[JMap[Any,Int]] = null
    var columnHeaderValueCounter:Array[Int] = null
    var colHeaderValues:Array[Int] = null
    var measureFieldPosition = -1
    var key:IntArrayWrapperKey = null
    var currentValue:Any = -1
    var aggregatedData:JMap[IntArrayWrapperKey,Any] = null
    var matchesFilter = true
    var columnHeaderFields:Array[Field[_]] = null
    var columnHeaderFieldDefinitions:Array[FieldDefinition] = null

    while (dataCounter < dataLength) {
      dataRow = data(dataCounter)
      matchesFilter = true

      while (matchesFilter && filterCounter < numFilterCols) {
        value = dataRow(filterFieldPositions(filterCounter))
        val fieldDefinition = filterFieldDefinitions(filterCounter)
        val field = filterFields(filterCounter).asInstanceOf[Field[fieldDefinition.V]]
        matchesFilter = field.filter.matches(value.asInstanceOf[fieldDefinition.V])
        lookUp = filtersLookUp(filterCounter)
        intForValue = lookUp.get(value)
        if (intForValue == 0) {
          fieldsValueCounterIndex = filtersValueCounter(filterCounter)
          newCounter = fieldsValueCounter(fieldsValueCounterIndex) + 1
          fieldsValueCounter(fieldsValueCounterIndex) = newCounter
          lookUp.put(value, newCounter)
          fieldValuesBitSets(field) += newCounter
        } else {
          fieldValuesBitSets(field) += intForValue
        }
        filterCounter += 1
      }
      filterCounter = 0

      rowHeaderValues = new Array[Int](numRowHeaderCols)
      while (matchesFilter && rowHeaderCounter < numRowHeaderCols) {
        value = dataRow(rowHeaderFieldPositions(rowHeaderCounter))
        val fieldDefinition = rowHeaderFieldDefinitions(rowHeaderCounter)
        val field = rowHeaderFields(rowHeaderCounter).asInstanceOf[Field[fieldDefinition.V]]
        matchesFilter = field.filter.matches(value.asInstanceOf[fieldDefinition.V])
        lookUp = rowHeadersLookUp(rowHeaderCounter)
        intForValue = lookUp.get(value)
        if (intForValue == 0) {
          fieldsValueCounterIndex = rowHeadersValueCounter(rowHeaderCounter)
          newCounter = fieldsValueCounter(fieldsValueCounterIndex) + 1
          fieldsValueCounter(fieldsValueCounterIndex) = newCounter
          lookUp.put(value, newCounter)
          rowHeaderValues(rowHeaderCounter) = newCounter
          fieldValuesBitSets(field) += newCounter
        } else {
          rowHeaderValues(rowHeaderCounter) = intForValue
          fieldValuesBitSets(field) += intForValue
        }
        rowHeaderCounter += 1
      }
      rowHeaderCounter = 0
      if (matchesFilter) {
        rowHeaders += new IntArrayWrapper(rowHeaderValues)
      }

      while (matchesFilter && pathsCounter < numPaths) {
        aggregatedData = aggregatedDataForPath(pathsCounter)
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
            if (intForValue == 0) {
              fieldsValueCounterIndex = columnHeaderValueCounter(colHeaderColCounter)
              newCounter = fieldsValueCounter(fieldsValueCounterIndex) + 1
              fieldsValueCounter(fieldsValueCounterIndex) = newCounter
              lookUp.put(value, newCounter)
              colHeaderValues(colHeaderColCounter) = newCounter
              fieldValuesBitSets(field) += newCounter
            } else {
              colHeaderValues(colHeaderColCounter) = intForValue
              fieldValuesBitSets(field) += intForValue
            }
          }
          colHeaderColCounter += 1
        }
        colHeaderColCounter = 0
        if (matchesFilter) {
          colHeaders(pathsCounter) += new IntArrayWrapper(colHeaderValues)

          measureFieldPosition = measureFieldPositions(pathsCounter)
          // If there isn't a measure field there is no need to update the aggregated data
          if (measureFieldPosition >= 0) {
            value = dataRow(measureFieldPosition)
            key = new IntArrayWrapperKey(rowHeaderValues, colHeaderValues)
            currentValue = aggregatedData.get(key)
            val fieldDefinition = measureFieldDefinitions(pathsCounter)
            if (currentValue == null) {
              newDataValue = fieldDefinition.combiner.combine(
                fieldDefinition.combiner.initialCombinedValue,
                value.asInstanceOf[fieldDefinition.V]
              )
            } else {
              newDataValue = fieldDefinition.combiner.combine(
                currentValue.asInstanceOf[fieldDefinition.C],
                value.asInstanceOf[fieldDefinition.V]
              )
            }
            aggregatedData.put(key, newDataValue)
          } else if (measureFieldPosition == -2) {
            // Count field
            value = 1
            key = new IntArrayWrapperKey(rowHeaderValues, colHeaderValues)
            currentValue = aggregatedData.get(key)
            val fieldDefinition = measureFieldDefinitions(pathsCounter)
            if (currentValue == null) {
              newDataValue = fieldDefinition.combiner.combine(
                fieldDefinition.combiner.initialCombinedValue,
                value.asInstanceOf[fieldDefinition.V]
              )
            } else {
              newDataValue = fieldDefinition.combiner.combine(
                currentValue.asInstanceOf[fieldDefinition.C],
                value.asInstanceOf[fieldDefinition.V]
              )
            }
            aggregatedData.put(key, newDataValue)
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
        values(hashMap.get(key)) = key
      }
      fieldID -> values
    }}

    // If there are no row fields or measure fields there with be an empty row in the headers that isn't needed so
    // remove it
    val rowHeadersToUse = if (rowHeaderFieldIDs.nonEmpty || columnHeaderPathsMeasureOptions.exists(_.isDefined)) {
      rowHeaders.toArray.map(_.array)
    } else {
      Array.empty[Array[Int]]
    }

    import JavaConversions._
    val pathData = new Array[PathData](numPaths)
    pathsCounter = 0
    while (pathsCounter < numPaths) {
      val colHeadersForPath:Array[IntArrayWrapper] = colHeaders(pathsCounter).toArray
      val numColHeadersForPath = colHeadersForPath.length
      val colHeadersArray:Array[Array[Int]] = new Array[Array[Int]](numColHeadersForPath)
      var colHeadersForPathCounter = 0
      while (colHeadersForPathCounter < numColHeadersForPath) {
        colHeadersArray(colHeadersForPathCounter) = colHeadersForPath(colHeadersForPathCounter).array
        colHeadersForPathCounter += 1
      }
      val dataForPath:JMap[IntArrayWrapperKey, Any] = aggregatedDataForPath(pathsCounter)
      pathData(pathsCounter) = PathData(colHeadersArray, dataForPath.toMap)
      pathsCounter += 1
    }
    val fieldValues = FieldValues(fieldValuesBitSets.map{case (field,bitSet) => field -> bitSet.toArray}.toMap)
    val resultDetails = ResultState(FilterState(isFiltered=true), SortState.allUnsorted(pathData.length))
    Result(rowHeadersToUse, pathData, fieldValues, valueLookUp, resultDetails)
  }
}
