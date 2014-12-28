package com.openaf.table.server

import org.scalatest.FunSuite

class IntArraySetTest extends FunSuite {
  test("test contains") {
    val set = new IntArraySet(0, 3)
    val values1 = Array(1,2,3,0)

    assert(set.contains(values1) === false)

    set += values1
    assert(set.contains(values1) === true)
    assert(set.contains(Array(1,2,3)) === true)
    assert(set.contains(Array(1,2,3,4,5,6)) === true)
    assert(set.contains(Array(3,2,1)) === false)
    assert(set.contains(Array(3,2,1,0)) === false)
  }

}
