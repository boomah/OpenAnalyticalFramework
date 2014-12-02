package com.openaf.table.server.datasources

import com.openaf.table.lib.api._
import java.util.{HashMap => JMap}

/**
 * Efficient way to determine whether a path is collapsed when used with the RawRowBasedTableDataSource
 */
private[datasources] class RawTableDataSourceCollapsedState(field:Field[_], fieldIndex:Int,
                                                            rowHeadersLookUp:Array[JMap[Any,MutableInt]],
                                                            rowHeadersValueCounter:Array[Int],
                                                            fieldsValueCounter:Array[Int]) {
  private val (intPaths, isCollapsed) = {
    val (paths, isCollapsed) = field.totals.collapsedState match {
      case AllExpanded(collapsedPaths) => (collapsedPaths, false)
      case AllCollapsed(expandedPaths) => (expandedPaths, true)
    }

    var lookUp:JMap[Any,MutableInt] = null
    var intForValue:MutableInt = null
    var fieldsValueCounterIndex = 0

    // TODO - maybe this should be converted into a while loop
    // Ignore paths of the wrong length and convert the path values to the appropriate int value
    val intPaths:Array[Array[Int]] = paths.filter(_.pathValues.length == (fieldIndex + 1)).map(path => {
      path.pathValues.zipWithIndex.map{case (value,rowHeaderCounter)  => {
        lookUp = rowHeadersLookUp(rowHeaderCounter)
        intForValue = lookUp.get(value)
        if (intForValue == null) {
          intForValue = new MutableInt
          fieldsValueCounterIndex = rowHeadersValueCounter(rowHeaderCounter)
          intForValue.int = fieldsValueCounter(fieldsValueCounterIndex) + 1
          fieldsValueCounter(fieldsValueCounterIndex) = intForValue.int
          lookUp.put(value, intForValue)
        }
        intForValue.int
      }}
    })(collection.breakOut)

    (intPaths, isCollapsed)
  }

  private val numPaths = intPaths.length
  private var pathCounter = fieldIndex
  private var pathFound = false
  private var intPath:Array[Int] = null

  private var pathElementCounter = 0
  private var pathElementMatches = true

  def collapsed(path:Array[Int]) = {
    if (intPaths.length == 0) {
      isCollapsed
    } else {
      pathCounter = 0
      pathFound = false
      while (!pathFound && pathCounter < numPaths) {
        pathElementCounter = fieldIndex
        pathElementMatches = true
        intPath = intPaths(pathCounter)
        // Check the path from the right side as that can tell us straight away if we stand a chance of matching
        while (pathElementMatches && pathElementCounter >= 0) {
          pathElementMatches = path(pathElementCounter) == intPath(pathElementCounter)
          pathElementCounter -= 1
        }
        pathFound = pathElementMatches
        pathCounter += 1
      }
      (isCollapsed && !pathFound) || (!isCollapsed && pathFound)
    }
  }
}
