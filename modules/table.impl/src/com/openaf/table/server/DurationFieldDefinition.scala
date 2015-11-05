package com.openaf.table.server

import java.time.{LocalDate, Duration}

import com.openaf.table.lib.api._

class DurationFieldDefinition(val defaultField:Field[Duration]) extends FieldDefinition {
  override type V = Duration
  override type C = Duration
  override val primaryKey = false
  override val ordering = DurationOrdering
  override def combiner = new DurationCombiner
  override def combinerFromType(combinerType:CombinerType) = {
    combinerType match {
      case Sum => combiner
      case Mean => new MeanDurationCombiner
      case Median => combiner
      case Min => new MinDurationCombiner
      case Max => new MaxDurationCombiner
    }
  }
  override val parser = DurationParser
}

object DurationFieldDefinition {
  def apply(defaultField:Field[Duration]) = new DurationFieldDefinition(defaultField)
}

class LocalDateFieldDefinition(override val defaultField:Field[LocalDate]) extends FieldDefinition {
  override type V = LocalDate
  override type C = Set[LocalDate]
  override val primaryKey = false
  override val ordering = LocalDateOrdering
  override def combiner = new LocalDateCombiner
  override val parser = LocalDateParser
}

object LocalDateFieldDefinition {
  def apply(defaultField:Field[LocalDate]) = new LocalDateFieldDefinition(defaultField)
}