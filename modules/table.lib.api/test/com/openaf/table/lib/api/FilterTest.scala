package com.openaf.table.lib.api

import org.scalatest.FunSuite

class FilterTest extends FunSuite {
  val nick = "Nick"
  val paul = "Paul"
  val ally = "Ally"

  test("RetainAllFilter") {
    val noFilter = RetainAllFilter[String]()
    assert(noFilter.matches(nick) === true)
    assert(noFilter.matches("Anything") === true)
  }

  test("RejectAllFilter") {
    val alwaysFilter = RejectAllFilter[String]()
    assert(alwaysFilter.matches(nick) === false)
    assert(alwaysFilter.matches("Anything") === false)
  }

  test("RetainFilter") {
    val containsFilter = RetainFilter[String](Set(nick, paul, ally))
    assert(containsFilter.matches(nick) === true)
    assert(containsFilter.matches(paul) === true)
    assert(containsFilter.matches(ally) === true)
    assert(containsFilter.matches("Other") === false)
  }

  test("RejectFilter") {
    val notContainsFilter = RejectFilter[String](Set(nick, paul, ally))
    assert(notContainsFilter.matches(nick) === false)
    assert(notContainsFilter.matches(paul) === false)
    assert(notContainsFilter.matches(ally) === false)
    assert(notContainsFilter.matches("Other 1") === true)
    assert(notContainsFilter.matches("Other 2") === true)
  }
}
