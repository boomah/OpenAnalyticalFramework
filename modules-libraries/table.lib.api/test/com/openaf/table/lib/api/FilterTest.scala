package com.openaf.table.lib.api

import org.scalatest.FunSuite

class FilterTest extends FunSuite {
  val nick = "Nick"
  val paul = "Paul"
  val ally = "Ally"

  test("NoFilter") {
    val noFilter = NoFilter[String]()
    assert(noFilter.matches(nick) === true)
    assert(noFilter.matches("Anything") === true)
  }

  test("AlwaysFilter") {
    val alwaysFilter = AlwaysFilter[String]()
    assert(alwaysFilter.matches(nick) === false)
    assert(alwaysFilter.matches("Anything") === false)
  }

  test("ContainsFilter") {
    val containsFilter = ContainsFilter[String](Set(nick, paul, ally))
    assert(containsFilter.matches(nick) === true)
    assert(containsFilter.matches(paul) === true)
    assert(containsFilter.matches(ally) === true)
    assert(containsFilter.matches("Other") === false)
  }

  test("NotContainsFilter") {
    val notContainsFilter = NotContainsFilter[String](Set(nick, paul, ally))
    assert(notContainsFilter.matches(nick) === false)
    assert(notContainsFilter.matches(paul) === false)
    assert(notContainsFilter.matches(ally) === false)
    assert(notContainsFilter.matches("Other 1") === true)
    assert(notContainsFilter.matches("Other 2") === true)
  }
}
