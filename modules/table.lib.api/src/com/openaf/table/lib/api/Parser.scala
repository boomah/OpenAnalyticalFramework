package com.openaf.table.lib.api

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, Duration}

trait Parser[V] {
  def parse(string:String):V
}

object NullParser extends Parser[Null] {
  override def parse(string:String) = null
}

object AnyParser extends Parser[Any] {
  override def parse(string:String) = string
}

object StringParser extends Parser[String] {
  override def parse(string:String) = string
}

object IntegerParser extends Parser[Integer] {
  override def parse(string:String) = Integer.valueOf(string)
}

object IntParser extends Parser[Int] {
  override def parse(string:String) = string.toInt
}

object DurationParser extends Parser[Duration] {
  override def parse(string:String) = {
    // TODO - for now this only handles mm:ss format. Need to change it to be more general.
    val components = string.split(":")
    Duration.ofSeconds(components(0).toInt * 60 + components(1).toInt)
  }
}

object LocalDateParser extends Parser[LocalDate] {
  private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  override def parse(string:String) = {
    // TODO - for now this only handles the dd/MM/yyyy format. It should be able to handle more.
    LocalDate.parse(string, formatter)
  }
}
