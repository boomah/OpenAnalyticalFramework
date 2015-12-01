package com.openaf.testdata.api

import com.openaf.table.lib.api.Parser

/**
 * Wraps a string. Used for testing a custom renderer.
 */
case class StringWrapper(string:String)

object StringWrapperParser extends Parser[StringWrapper] {
  override def parse(string:String) = StringWrapper(string)
}
