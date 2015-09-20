package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import java.util
import scala.collection.mutable
import scala.util.Random
import scala.math.Ordering.Implicits._

class IntArraySetTest extends FunSuite {
  class TestIntArrayWrapper(val array:Array[Int]) {
    override val hashCode = util.Arrays.hashCode(array)
    override def equals(other:Any) = util.Arrays.equals(array, other.asInstanceOf[TestIntArrayWrapper].array)
  }

  test("Same behaviour as normal set when adding random data") {
    val numberOfElements = 100000
    val widthOfData = 5
    val random = new Random
    val testData = (0 until numberOfElements).map(_ => {
      val array = new Array[Int](widthOfData)
      (0 until widthOfData).foreach(i => array(i) = random.nextInt(20))
      array
    }).toArray

    val intArraySet = new IntArraySet(widthOfData)
    testData.foreach(array => intArraySet += array)
    val intArraySetResult = intArraySet.toArray.map(_.toList).toList.sorted

    val hashSet = new mutable.HashSet[TestIntArrayWrapper]
    testData.foreach(array => hashSet += new TestIntArrayWrapper(array))
    val hashSetResult = hashSet.toList.map(_.array.toList).sorted

    assert(intArraySetResult === hashSetResult)
  }
}
