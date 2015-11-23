package com.openaf.table.server

import java.time.{YearMonth, LocalDate, Duration}

import scala.collection.mutable

object DurationCombiner {
  // When duration is being combined sometimes NoValue is present. If there is just a NoValue then when value is called
  // on the combiner it will try and divide by zero or return a zero duration. In these cases this specialDuration
  // should be returned and the renderer should handle it in some special way such as an empty string.
  val SpecialDuration = Duration.ofSeconds(Long.MinValue)
  val MaxDuration = Duration.ofSeconds(Long.MaxValue)
  val MinDuration = SpecialDuration
  val ZeroDuration = Duration.ofNanos(0)
}

import DurationCombiner._

class DurationCombiner extends Combiner[Duration,Duration] {
  private var runningDuration = ZeroDuration
  override def combine(value:Duration) = {runningDuration = runningDuration.plus(value)}
  override def value = if (runningDuration == ZeroDuration) SpecialDuration else runningDuration
}

class MeanDurationCombiner extends Combiner[Duration,Duration] {
  private var counter = 0
  private var runningDuration = Duration.ofNanos(0)
  override def combine(value:Duration) = {
    counter += 1
    runningDuration = runningDuration.plus(value)
  }
  override def value = if (counter == 0) SpecialDuration else runningDuration.dividedBy(counter)
}

class MinDurationCombiner extends Combiner[Duration,Duration] {
  private var minValue = MaxDuration
  override def combine(value:Duration) = {
    if (value.compareTo(minValue) < 0) {minValue = value}
  }
  override def value = if (minValue == MaxDuration) SpecialDuration else minValue
}

class MaxDurationCombiner extends Combiner[Duration,Duration] {
  private var maxValue = MinDuration
  override def combine(value:Duration) = {
    if (value.compareTo(maxValue) > 0) {maxValue = value}
  }
  override def value = if (maxValue == MinDuration) SpecialDuration else maxValue
}

class LocalDateCombiner extends Combiner[Set[LocalDate],LocalDate] {
  private val set = new mutable.HashSet[LocalDate]
  override def combine(value:LocalDate) = {set += value}
  override def value = set.toSet
}

class YearMonthCombiner extends Combiner[Set[YearMonth],YearMonth] {
  private val set = new mutable.HashSet[YearMonth]
  override def combine(value:YearMonth) = {set += value}
  override def value = set.toSet
}