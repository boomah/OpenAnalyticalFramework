package com.openaf.table.lib.api

sealed trait Filter[V] {
  def matches(value:V):Boolean

  /**
   * Indicates that this filter needs to be transformed
   */
  def shouldTransform:Boolean
  def enableTransform:Filter[V]
  def values:Set[V]
  def withTransformedValues[T](transformedValues:Set[T], transformedOrdering:Ordering[T]):Filter[T]
}
case class RetainAllFilter[V]() extends Filter[V] {
  override def matches(value:V) = true
  override def shouldTransform = false
  override def enableTransform = this
  override def values = Set.empty
  override def withTransformedValues[T](transformedValues:Set[T], transformedOrdering:Ordering[T]) = this.asInstanceOf[Filter[T]]
}
case class RejectAllFilter[V]() extends Filter[V] {
  override def matches(value:V) = false
  override def shouldTransform = false
  override def enableTransform = this
  override def values = Set.empty
  override def withTransformedValues[T](transformedValues:Set[T], transformedOrdering:Ordering[T]) = this.asInstanceOf[Filter[T]]
}
case class RetainFilter[V](values:Set[V], shouldTransform:Boolean=false) extends Filter[V] {
  override def matches(value:V) = values.contains(value)
  override def enableTransform = copy(shouldTransform = true)
  override def withTransformedValues[T](transformedValues:Set[T], transformedOrdering:Ordering[T]) = RetainFilter[T](transformedValues)
}
case class RejectFilter[V](values:Set[V], shouldTransform:Boolean=false) extends Filter[V] {
  override def matches(value:V) = !values.contains(value)
  override def enableTransform = copy(shouldTransform = true)
  override def withTransformedValues[T](transformedValues:Set[T], transformedOrdering:Ordering[T]) = RejectFilter[T](transformedValues)
}
case class OrderedFilter[V](value:V, ordering:Ordering[V], greaterThan:Boolean, andEqual:Boolean,
                            shouldTransform:Boolean=false) extends Filter[V] {
  override def matches(value:V) = {
    if (greaterThan && andEqual) {
      ordering.gteq(value, this.value)
    } else if (greaterThan) {
      ordering.gt(value, this.value)
    } else if (andEqual) {
      ordering.lteq(value, this.value)
    } else {
      ordering.lt(value, this.value)
    }
  }
  override def values = Set(value)
  override def enableTransform = copy(shouldTransform = true)
  override def withTransformedValues[T](transformedValues:Set[T], transformedOrdering:Ordering[T]) = {
    OrderedFilter[T](transformedValues.head, ordering = transformedOrdering, greaterThan = greaterThan, andEqual = andEqual)
  }
}