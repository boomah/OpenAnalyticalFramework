package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{FieldID, TableState}
import com.openaf.table.server.{NullFieldDefinition, FieldDefinitionGroup}
import java.util.{HashMap => JMap}
import scala.collection.{JavaConversions, mutable}

case class RawRowBasedTableDataSource(data:Array[Array[Any]], fieldIDs:Array[FieldID],
                                      fieldDefinitionGroup:FieldDefinitionGroup) extends TableDataSource {
  def result(tableState:TableState) = RawRowBasedTableDataSource.result(tableState, data, fieldIDs, fieldDefinitionGroup)
}

object RawRowBasedTableDataSource {
  // This is far from idiomatic Scala. Written this way for speed.
  // TODO - remove the need to keep converting to and from lists
  def result(tableState:TableState, data:Array[Array[Any]], fieldIDs:Array[FieldID],
             fieldDefinitionGroup:FieldDefinitionGroup) = {
    val allFieldIDs = tableState.distinctFieldIDs
    val fieldIDToLookUp:Map[FieldID,JMap[Any,Int]] = allFieldIDs.map(field => {
      val map = new JMap[Any,Int]
      map.put(field, 0)
      field -> map
    })(collection.breakOut)
    val fieldsValueCounter = new Array[Int](allFieldIDs.size)

    val rowHeaderFieldIDs = tableState.tableLayout.rowHeaderFieldIDs
    val numRowHeaderCols = rowHeaderFieldIDs.length
    var rowHeaderCounter = 0
    val rowHeaderFieldPositions = rowHeaderFieldIDs.map(fieldIDs.indexOf(_)).toArray
    val rowHeaders = new mutable.HashSet[List[Int]]
    val rowHeadersLookUp = rowHeaderFieldIDs.map(fieldIDToLookUp).toArray
    val rowHeadersValueCounter = rowHeaderFieldIDs.map(allFieldIDs.indexOf(_)).toArray

    val measureAreaPaths = tableState.tableLayout.measureAreaLayout.paths
    val numPaths = measureAreaPaths.length
    var pathsCounter = 0
    val numColumnHeaderColsPerPath = measureAreaPaths.map(_.fields.length).toArray
    var numColumnHeaderCols = -1
    var colHeaderColCounter = 0
    val colHeaderFieldsPositions = measureAreaPaths.map(_.fields.map(field => fieldIDs.indexOf(field.id)).toArray).toArray
    val colHeaders = Array.fill(numPaths)(new mutable.HashSet[List[Int]])
    val colHeadersLookUps = measureAreaPaths.map(path =>
      path.fields.map(field => fieldIDToLookUp(field.id)).toArray
    ).toArray
    val colHeadersValuesCounter = measureAreaPaths.map(path =>
      path.fields.map(field => allFieldIDs.indexOf(field.id)).toArray
    ).toArray

    val measureAreaMeasureFieldPositions = measureAreaPaths.map(_.measureFieldIndex).toArray
    val measureAreaPathsMeasureOptions = measureAreaPaths.map(_.measureFieldOption).toArray
    val measureFieldPositions = measureAreaPathsMeasureOptions.map{
      case Some(field) => fieldIDs.indexOf(field.id)
      case _ => -1
    }
    val measureFieldDefinitions = measureAreaPathsMeasureOptions.map{
      case Some(field) => fieldDefinitionGroup.fieldDefinition(field.id)
      case _ => NullFieldDefinition
    }

    val aggregatedDataForPath = Array.fill(numPaths)(new JMap[(List[Int],List[Int]),Any])

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
    var measureAreaFieldPositions:Array[Int] = null
    var measureAreaLookUp:Array[JMap[Any,Int]] = null
    var measureAreaValueCounter:Array[Int] = null
    var colHeaderValues:Array[Int] = null
    var measureFieldPosition = -1
    var key:(List[Int],List[Int]) = null
    var currentValue:Any = -1
    var aggregatedData:JMap[(List[Int],List[Int]),Any] = null

    while (dataCounter < dataLength) {
      dataRow = data(dataCounter)

      rowHeaderValues = new Array[Int](numRowHeaderCols)
      while (rowHeaderCounter < numRowHeaderCols) {
        value = dataRow(rowHeaderFieldPositions(rowHeaderCounter))
        lookUp = rowHeadersLookUp(rowHeaderCounter)
        intForValue = lookUp.get(value)
        if (intForValue == 0) {
          fieldsValueCounterIndex = rowHeadersValueCounter(rowHeaderCounter)
          newCounter = fieldsValueCounter(fieldsValueCounterIndex) + 1
          fieldsValueCounter(fieldsValueCounterIndex) = newCounter
          lookUp.put(value, newCounter)
          rowHeaderValues(rowHeaderCounter) = newCounter
        } else {
          rowHeaderValues(rowHeaderCounter) = intForValue
        }
        rowHeaderCounter += 1
      }
      rowHeaderCounter = 0
      rowHeaders += rowHeaderValues.toList

      while (pathsCounter < numPaths) {
        aggregatedData = aggregatedDataForPath(pathsCounter)
        measureFieldIndex = measureAreaMeasureFieldPositions(pathsCounter)
        measureAreaFieldPositions = colHeaderFieldsPositions(pathsCounter)
        measureAreaLookUp = colHeadersLookUps(pathsCounter)
        measureAreaValueCounter = colHeadersValuesCounter(pathsCounter)
        numColumnHeaderCols = numColumnHeaderColsPerPath(pathsCounter)
        colHeaderValues = new Array[Int](numColumnHeaderCols)
        while (colHeaderColCounter < numColumnHeaderCols) {
          if (colHeaderColCounter != measureFieldIndex) {
            value = dataRow(measureAreaFieldPositions(colHeaderColCounter))
            lookUp = measureAreaLookUp(colHeaderColCounter)
            intForValue = lookUp.get(value)
            if (intForValue == 0) {
              fieldsValueCounterIndex = measureAreaValueCounter(colHeaderColCounter)
              newCounter = fieldsValueCounter(fieldsValueCounterIndex) + 1
              fieldsValueCounter(fieldsValueCounterIndex) = newCounter
              lookUp.put(value, newCounter)
              colHeaderValues(colHeaderColCounter) = newCounter
            } else {
              colHeaderValues(colHeaderColCounter) = intForValue
            }
          }
          colHeaderColCounter += 1
        }
        colHeaderColCounter = 0
        colHeaders(pathsCounter) += colHeaderValues.toList

        measureFieldPosition = measureFieldPositions(pathsCounter)
        // If there isn't a measure field there is no need to update the aggregated data
        if (measureFieldPosition != -1) {
          value = dataRow(measureFieldPosition)
          key = (rowHeaderValues.toList, colHeaderValues.toList)
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
        pathsCounter += 1
      }
      pathsCounter = 0
      dataCounter += 1
    }

    import JavaConversions._
    val valueLookUp = fieldIDToLookUp.map{case (fieldID, hashMap) => {
      val values = new Array[Any](hashMap.size)
      hashMap.foreach(element => values(element._2) = element._1)
      fieldID -> values
    }}

    // If there are no row fields or measure fields there with be an empty row in the headers that isn't needed so
    // remove it
    val rowHeadersToUse = if (rowHeaderFieldIDs.nonEmpty || measureAreaPathsMeasureOptions.exists(_.isDefined)) {
      rowHeaders.toArray.map(_.toArray)
    } else {
      Array.empty[Array[Int]]
    }

    val pathData = colHeaders.map(_.toArray.map(_.toArray)).zip(aggregatedDataForPath).map{
      case (colData, mainData) => PathData(colData, mainData.toMap)
    }
    val resultDetails = ResultDetails(SortDetails.allUnsorted(pathData.length))
    Result(rowHeadersToUse, pathData, valueLookUp, resultDetails)
  }
}
