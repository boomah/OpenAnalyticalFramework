package com.openaf.table.lib.api

sealed trait Filter[T] {
  def matches(value:T):Boolean
}
case class NoFilter[T]() extends Filter[T] {
  def matches(value:T) = true
}
case class SpecifiedFilter[T](values:Set[T]) extends Filter[T] {
  def matches(value:T) = values.contains(value)
}
