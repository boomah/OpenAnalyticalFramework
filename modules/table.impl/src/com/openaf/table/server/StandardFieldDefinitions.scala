package com.openaf.table.server

import com.openaf.table.lib.api.StandardFields._

object StandardFieldDefinitions {
  val CountFieldDefinition = new IncrementingFieldDefinition(CountField)

  val All = List(CountFieldDefinition)
}
