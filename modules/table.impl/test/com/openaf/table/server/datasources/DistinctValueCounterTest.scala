package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import scala.collection.mutable
import scala.util.Random
import DistinctValueCounterTest._

class DistinctValueCounterTest extends FunSuite {
  test("Same behaviour as a standard map when adding random data") {
    val numberOfElements = 50000
    val stringWidth = 6
    val strings = (0 until numberOfElements).map(_ => randomString(stringWidth))

    val valueCounter = new DistinctValueCounter
    val standardMap = new mutable.HashMap[Any,Int]
    var counter = 0

    strings.foreach(string => {
      valueCounter.intForValue(string)
      if (!standardMap.contains(string)) {
        standardMap += (string -> counter)
        counter += 1
      }
    })

    strings.foreach(string => {
      valueCounter.intForValue(string)
      if (!standardMap.contains(string)) {
        standardMap += (string -> counter)
        counter += 1
      }
    })

    val valueCounterData = valueCounter.toArray.toList
    val mapData = new Array[Any](standardMap.size)
    standardMap.foreach{case (value,int) => mapData(int) = value}

    assert(valueCounterData === mapData.toList)
  }


}

object DistinctValueCounterTest {
  private val letters = ('a' to 'z').toArray
  private val random = new Random
  def randomString(numLetters:Int):String = (0 until numLetters).map(_ => letters(random.nextInt(letters.length))).mkString
}
