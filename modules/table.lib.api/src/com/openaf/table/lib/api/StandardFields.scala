package com.openaf.table.lib.api

object StandardFields {
  val CountField = Field[Integer]("count", Measure)

  val AllFields = List(CountField)
  val AllFieldIDs = AllFields.map(_.id)
}
