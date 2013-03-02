package com.openaf.table.api

case class MeasureAreaLayout() {
  def allFields:Set[Field] = Set.empty
}
object MeasureAreaLayout {
  val Blank = MeasureAreaLayout()
}
