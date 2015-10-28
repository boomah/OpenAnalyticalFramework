package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{Combiner, Field}
import com.openaf.table.server.IntFieldDefinition
import org.scalatest.FunSuite

import scala.collection.mutable
import scala.util.Random

class AggregatorTest extends FunSuite {
  test("Same behaviour as a normal map") {
    val numberOfElements = 100000
    val widthOfData = 5
    val random = new Random
    val keys = (0 until numberOfElements).map(_ => {
      val rowArray = new Array[Int](widthOfData)
      val columnArray = new Array[Int](widthOfData)
      (0 until widthOfData).foreach(i => rowArray(i) = random.nextInt(20))
      (0 until widthOfData).foreach(i => columnArray(i) = random.nextInt(20))
      (rowArray, columnArray)
    }).toArray

    val map = new mutable.HashMap[(List[Int],List[Int]),Combiner[_,_]]
    val aggregator = new Aggregator(widthOfData)
    val fieldDefinition = new IntFieldDefinition(Field[Int]("count"))

    keys.foreach{case (row,column) =>
      val value = random.nextInt(1000)
      aggregator.combine(value, fieldDefinition, row, column)

      val mapKey = (row.toList, column.toList)
      map.get(mapKey) match {
        case Some(combiner) => combiner.asInstanceOf[Combiner[Int,Int]].combine(value)
        case None =>
          val combiner = fieldDefinition.combiner
          combiner.combine(value)
          map(mapKey) = combiner
      }
    }

    keys.reverse.foreach{case (row,column) =>
      val value = random.nextInt(2000)
      aggregator.combine(value, fieldDefinition, row, column)

      val mapKey = (row.toList, column.toList)
      map.get(mapKey) match {
        case Some(combiner) => combiner.asInstanceOf[Combiner[Int,Int]].combine(value)
        case None =>
          val combiner = fieldDefinition.combiner
          combiner.combine(value)
          map(mapKey) = combiner
      }
    }

    val result = keys.forall{case (row,column) => aggregator(row,column) == map((row.toList,column.toList)).value}
    assert(result, "Aggregator not matching a standard map")
  }
}
