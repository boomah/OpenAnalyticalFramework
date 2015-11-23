package com.openaf.table.server.datasources

import com.openaf.table.lib.api._
import com.openaf.table.server.{Combiner, IntFieldDefinition}
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
      aggregator.combine(value, fieldDefinition, Sum, row, column)

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
      aggregator.combine(value, fieldDefinition, Sum, row, column)

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

  test("Average aggregation") {
    val rowKey = Array(0,1,2)
    val columnKey = Array(0,1,2)
    val aggregator = new Aggregator(rowKey.length)
    val fieldDefinition = new IntFieldDefinition(Field[Int]("count"))

    aggregator.combine(1, fieldDefinition, Mean, rowKey, columnKey)
    aggregator.combine(5, fieldDefinition, Mean, rowKey, columnKey)
    aggregator.combine(8, fieldDefinition, Mean, rowKey, columnKey)
    aggregator.combine(9, fieldDefinition, Mean, rowKey, columnKey)

    assert(aggregator(rowKey, columnKey) === 5, "Aggregator not averaging properly")
  }

  test("Min aggregation") {
    val rowKey = Array(0,1,2)
    val columnKey = Array(0,1,2)
    val aggregator = new Aggregator(rowKey.length)
    val fieldDefinition = new IntFieldDefinition(Field[Int]("count"))

    aggregator.combine(5, fieldDefinition, Min, rowKey, columnKey)
    aggregator.combine(1, fieldDefinition, Min, rowKey, columnKey)
    aggregator.combine(8, fieldDefinition, Min, rowKey, columnKey)
    aggregator.combine(9, fieldDefinition, Min, rowKey, columnKey)

    assert(aggregator(rowKey, columnKey) === 1, "Aggregator not mining properly")
  }

  test("Max aggregation") {
    val rowKey = Array(0,1,2)
    val columnKey = Array(0,1,2)
    val aggregator = new Aggregator(rowKey.length)
    val fieldDefinition = new IntFieldDefinition(Field[Int]("count"))

    aggregator.combine(5, fieldDefinition, Max, rowKey, columnKey)
    aggregator.combine(1, fieldDefinition, Max, rowKey, columnKey)
    aggregator.combine(9, fieldDefinition, Max, rowKey, columnKey)
    aggregator.combine(8, fieldDefinition, Max, rowKey, columnKey)

    assert(aggregator(rowKey, columnKey) === 9, "Aggregator not maxing properly")
  }

  test("Hash clash resolves correctly") {
    val columnKey = Array(0, 0)
    val rowKey1 = Array(1, 2, 96)
    val rowKey2 = Array(2, 1, -2)

    val aggregator = new Aggregator(rowKey1.length)
    val fieldDefinition = new IntFieldDefinition(Field[Int]("count"))

    aggregator.combine(5, fieldDefinition, Max, rowKey1, columnKey)
    aggregator.combine(7, fieldDefinition, Max, rowKey2, columnKey)

    assert(aggregator(rowKey1, columnKey) === 5, "")
    assert(aggregator(rowKey2, columnKey) === 7, "")
  }
}
