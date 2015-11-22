package com.openaf.table.lib.api

import java.time.format.DateTimeFormatter
import java.time.{YearMonth, LocalDate, Duration}
import java.lang.{Double => JDouble}

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

object DoubleParser extends Parser[Double] {
  override def parse(string:String) = string.toDouble
}

object JDoubleParser extends Parser[JDouble] {
  override def parse(string:String) = JDouble.valueOf(string)
}

object DurationParser extends Parser[Duration] {
  override def parse(string:String) = {
    // TODO - for now this only handles hh:mm:ss format. Need to change it to be more general.
    val components = string.split(":")
    val seconds = components.reverse.zipWithIndex.map{case (intString,index) => intString.toInt * math.pow(60,index)}.sum.toLong
    Duration.ofSeconds(seconds)
  }
}

object LocalDateParser extends Parser[LocalDate] {
  private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  override def parse(string:String) = {
    // TODO - for now this only handles the dd/MM/yyyy format. It should be able to handle more.
    LocalDate.parse(string, formatter)
  }
}

object YearMonthParser extends Parser[YearMonth] {
  private val formatter = DateTimeFormatter.ofPattern("MMM-yyyy")
  override def parse(string:String) = {
    // TODO - for now this only handles the MMM-yyyy format. It should be able to handle more.
    YearMonth.parse(string, formatter)
  }
}
