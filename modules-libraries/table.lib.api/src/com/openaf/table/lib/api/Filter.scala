package com.openaf.table.lib.api

sealed trait Filter[T] {
  def matches(value:T):Boolean
}
case class NoFilter[T]() extends Filter[T] {
  def matches(value:T) = true
}
case class AlwaysFilter[T]() extends Filter[T] {
  def matches(value:T) = false
}
case class ContainsFilter[T](values:Set[T]) extends Filter[T] {
  def matches(value:T) = values.contains(value)
}
case class NotContainsFilter[T](values:Set[T]) extends Filter[T] {
  def matches(value:T) = !values.contains(value)
}
