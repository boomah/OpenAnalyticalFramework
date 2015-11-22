package com.openaf.table.server

import java.lang.{Double => JDouble}
import java.time.{LocalDate, YearMonth}

import com.openaf.table.lib.api._

trait Transformer[V,T] {
  def transformerType:TransformerType[T]
  def transform(value:V):T
  def transformedFieldDefinition(currentFieldDefinition:FieldDefinition):FieldDefinition = currentFieldDefinition
}

object IdentityTransformer extends Transformer[Any,Any] {
  override def transformerType = IdentityTransformerType
  override def transform(value:Any) = value
}

object IntToDoubleTransformer extends Transformer[Int,Double] {
  override def transformerType = IntToDoubleTransformerType
  override def transform(value:Int) = value.toDouble
  override def transformedFieldDefinition(currentFieldDefinition:FieldDefinition) = {
    DoubleFieldDefinition(currentFieldDefinition.defaultField.asInstanceOf[Field[Double]])
  }
}

object IntegerToJDoubleTransformer extends Transformer[Integer,JDouble] {
  override def transformerType = IntegerToJDoubleTransformerType
  override def transform(value:Integer) = JDouble.valueOf(value.intValue)
  override def transformedFieldDefinition(currentFieldDefinition:FieldDefinition) =
    JDoubleFieldDefinition(currentFieldDefinition.defaultField.asInstanceOf[Field[JDouble]])
}

object LocalDateToYearMonthTransformer extends Transformer[LocalDate,YearMonth] {
  override def transformerType = LocalDateToYearMonthTransformerType
  override def transform(value:LocalDate) = YearMonth.of(value.getYear, value.getMonth)
  override def transformedFieldDefinition(currentFieldDefinition:FieldDefinition) =
    YearMonthFieldDefinition(currentFieldDefinition.defaultField.asInstanceOf[Field[YearMonth]])
}
