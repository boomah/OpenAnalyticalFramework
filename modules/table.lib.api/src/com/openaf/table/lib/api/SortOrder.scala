package com.openaf.table.lib.api

sealed trait SortOrder {
  def direction:Int
}

case object Ascending extends SortOrder {
  override val direction = 1
}

case object Descending extends SortOrder {
  override val direction = -1
}
