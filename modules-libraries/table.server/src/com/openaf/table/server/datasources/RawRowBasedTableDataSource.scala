package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{TableState, Field}
import com.openaf.table.server.FieldDefinitionGroup
import java.util.{HashMap => JMap}
import scala.collection.{JavaConversions, mutable}

case class RawRowBasedTableDataSource(data:Array[Array[Any]], fields:Array[Field],
                                      fieldDefinitionGroup:FieldDefinitionGroup) extends TableDataSource {

  // This is far from idiomatic Scala. Written this way for speed.
  // TODO - remove the need to keep converting to and from lists
  def result(tableState:TableState) = {
    val fieldsIDs = fields.map(_.id)
    val allFields = tableState.allFields.toList.map(_.id)
    val fieldToLookUp:Map[String,JMap[Any,Int]] = allFields.map(field => {
      val map = new JMap[Any,Int]
      map.put(field, 0)
      field -> map
    })(collection.breakOut)
    val fieldsValueCounter = new Array[Int](allFields.size)

    val rowHeaderFields = tableState.tableLayout.rowHeaderFields.map(_.id)
    val numRowHeaderCols = rowHeaderFields.length
    var rowHeaderCounter = 0
    val rowHeaderFieldPositions = rowHeaderFields.map(fieldsIDs.indexOf(_)).toArray
    val rowHeaders = new mutable.HashSet[List[Int]]
    val rowHeadersLookUp = rowHeaderFields.map(fieldToLookUp).toArray
    val rowHeadersValueCounter = rowHeaderFields.map(allFields.indexOf(_)).toArray

    val measureAreaPaths = tableState.tableLayout.measureAreaLayout.reversePaths
    val numPaths = measureAreaPaths.length
    var pathsCounter = 0
    val numColumnHeaderColsPerPath = measureAreaPaths.map(_.fields.length).toArray
    var numColumnHeaderCols = -1
    var colHeaderColCounter = 0
    val colHeaderFieldsPositions = measureAreaPaths.map(_.fields.map(field => fieldsIDs.indexOf(field.id)).toArray).toArray
    val colHeaders = Array.fill(numPaths)(new mutable.HashSet[List[Int]])
    val colHeadersLookUps = measureAreaPaths.map(path =>
      path.fields.map(field => fieldToLookUp(field.id)).toArray
    ).toArray
    val colHeadersValuesCounter = measureAreaPaths.map(path =>
      path.fields.map(field => allFields.indexOf(field.id)).toArray
    ).toArray

    val measureAreaMeasureFieldPositions = measureAreaPaths.map(_.measureFieldIndex).toArray
    val measureAreaPathsMeasureOptions = measureAreaPaths.map(_.measureFieldOption).toArray
    val measureFieldPositions = measureAreaPathsMeasureOptions.map{
      case Some(field) => fieldsIDs.indexOf(field.id)
      case _ => -1
    }

    val aggregatedDataForPath = Array.fill(numPaths)(new JMap[(List[Int],List[Int]),Any])

    val dataLength = data.length
    var dataCounter = 0

    var dataRow:Array[Any] = null
    var rowHeaderValues:Array[Int] = null
    var value:Any = -1
    var newDataValue = -1
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
          if (currentValue == null) {
            aggregatedData.put(key, value)
          } else {
            newDataValue = currentValue.asInstanceOf[Int] + value.asInstanceOf[Int]
            aggregatedData.put(key, newDataValue)
          }
        }
        pathsCounter += 1
      }
      pathsCounter = 0
      dataCounter += 1
    }

    import JavaConversions._
    val valueLookUp = fieldToLookUp.map{case (field, hashMap) => {
      val values = new Array[Any](hashMap.size)
      hashMap.foreach(element => values(element._2) = element._1)
      field -> values
    }}

    // If there are no row fields there with be an empty row in the headers that isn't needed so remove it
    val rowHeadersToUse = if (rowHeaderFields.isEmpty) Array.empty[Array[Int]] else rowHeaders.toArray.map(_.toArray)

    val pathData = colHeaders.map(_.toArray.map(_.toArray)).zip(aggregatedDataForPath).map{
      case (colData, mainData) => PathData(colData, mainData.toMap)
    }
    Result(rowHeadersToUse, pathData, valueLookUp)
  }
}
