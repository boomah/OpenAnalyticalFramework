package com.openaf.table.api

case class TableData(fieldGroups:FieldGroups, defaultTableState:TableState) {
  def fields = fieldGroups.groups.flatMap(_.fields).toSet
}
object TableData {
  val Empty = TableData(FieldGroups.Empty, TableState.Blank)
}
