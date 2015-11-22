package com.openaf.table.lib.api

sealed trait Filter[V] {
  def matches(value:V):Boolean

  /**
   * Indicates that this filter needs to be transformed
   */
  def shouldTransform:Boolean
  def enableTransform:Filter[V]
  def values:Set[V]
  def withTransformedValues[T](transformedValues:Set[T]):Filter[T]
}
case class RetainAllFilter[V]() extends Filter[V] {
  def matches(value:V) = true
  override def shouldTransform = false
  override def enableTransform = this
  override def values = Set.empty
  override def withTransformedValues[T](transformedValues:Set[T]) = this.asInstanceOf[Filter[T]]
}
case class RejectAllFilter[V]() extends Filter[V] {
  def matches(value:V) = false
  override def shouldTransform = false
  override def enableTransform = this
  override def values = Set.empty
  override def withTransformedValues[T](transformedValues:Set[T]) = this.asInstanceOf[Filter[T]]
}
case class RetainFilter[V](values:Set[V], shouldTransform:Boolean=false) extends Filter[V] {
  def matches(value:V) = values.contains(value)
  override def enableTransform = copy(shouldTransform = true)
  override def withTransformedValues[T](transformedValues:Set[T]) = RetainFilter[T](transformedValues)
}
case class RejectFilter[V](values:Set[V], shouldTransform:Boolean=false) extends Filter[V] {
  def matches(value:V) = !values.contains(value)
  override def enableTransform = copy(shouldTransform = true)
  override def withTransformedValues[T](transformedValues:Set[T]) = RejectFilter[T](transformedValues)
}
