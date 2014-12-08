package com.openaf.table.lib.api

sealed trait Filter[T] {
  def matches(value:T):Boolean
}
case class RetainAllFilter[T]() extends Filter[T] {
  def matches(value:T) = true
}
case class RejectAllFilter[T]() extends Filter[T] {
  def matches(value:T) = false
}
case class RetainFilter[T](values:Set[T]) extends Filter[T] {
  def matches(value:T) = values.contains(value)
}
case class RejectFilter[T](values:Set[T]) extends Filter[T] {
  def matches(value:T) = !values.contains(value)
}
