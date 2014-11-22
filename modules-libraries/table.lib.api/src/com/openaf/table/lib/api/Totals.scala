package com.openaf.table.lib.api

import java.util

case class Totals(top:Boolean=false, bottom:Boolean=false, collapsedState:CollapsedState=CollapsedState.Default)

object Totals {
  val Default = Totals()
}

object CollapsedState {
  val Default = AllExpanded(Set.empty)
}

sealed trait CollapsedState

case class AllExpanded(collapsed:Set[CollapsedStatePath]=Set.empty) extends CollapsedState
case class AllCollapsed(expanded:Set[CollapsedStatePath]=Set.empty) extends CollapsedState

class CollapsedStatePath(val pathValues:Array[Any]) {
  override val hashCode = util.Arrays.hashCode(pathValues.asInstanceOf[Array[AnyRef]])
  override def equals(other:Any) = util.Arrays.equals(
    pathValues.asInstanceOf[Array[AnyRef]],
    other.asInstanceOf[CollapsedStatePath].pathValues.asInstanceOf[Array[AnyRef]]
  )
  override def toString = s"CollapsedStatePath(${pathValues.toList.mkString("[",",","]")})"
}

object CollapsedStatePath {
  def apply(pathValues:Array[Any]) = new CollapsedStatePath(pathValues)
}