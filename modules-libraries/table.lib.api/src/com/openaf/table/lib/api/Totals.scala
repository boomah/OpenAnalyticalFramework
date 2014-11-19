package com.openaf.table.lib.api

import java.util

case class Totals(top:Boolean=false, bottom:Boolean=false, collapsedState:CollapsedState=CollapsedState.Default)

object Totals {
  val Default = Totals()
}

trait CollapsedState {
  def collapsed(path:CollapsedStatePath):Boolean
}

object CollapsedState {
  val Default = AllExpanded(Set.empty)
}

case class AllExpanded(collapsed:Set[CollapsedStatePath]) extends CollapsedState {
  def collapsed(path:CollapsedStatePath) = collapsed.contains(path)
}

case class AllCollapsed(expanded:Set[CollapsedStatePath]) extends CollapsedState {
  def collapsed(path:CollapsedStatePath) = !expanded.contains(path)
}

class CollapsedStatePath(val pathValues:Array[Any]) {
  override val hashCode = util.Arrays.hashCode(pathValues.asInstanceOf[Array[AnyRef]])
  override def equals(other:Any) = util.Arrays.equals(
    pathValues.asInstanceOf[Array[AnyRef]],
    other.asInstanceOf[CollapsedStatePath].pathValues.asInstanceOf[Array[AnyRef]]
  )
  override def toString = s"CollapsedStatePath(${pathValues.toList.mkString("[",",","]")})"
}