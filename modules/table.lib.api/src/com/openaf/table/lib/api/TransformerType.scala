package com.openaf.table.lib.api

import java.lang.{Double => JDouble}
import java.time.YearMonth

trait TransformerType[+T] {
  def name:String = {
    val className = getClass.getSimpleName.replace("$", "")
    if (className.length > 1) className.head.toLower + className.tail else className.toLowerCase
  }
}

case object IdentityTransformerType extends TransformerType[Any]

case object IntToDoubleTransformerType extends TransformerType[Double]

case object IntegerToJDoubleTransformerType extends TransformerType[JDouble]

case object LocalDateToYearMonthTransformerType extends TransformerType[YearMonth]