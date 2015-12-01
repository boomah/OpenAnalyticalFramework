package com.openaf.table.gui

import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.time.{YearMonth, LocalDate, Duration}
import java.util.Locale

import com.openaf.table.lib.api.{YearMonthParser, LocalDateParser, DurationParser}

case object DurationRenderer extends SpecialHandlingDurationRenderer {
  override def renderDuration(value:Duration, locale:Locale) = {
    val days = value.toDays
    val hours = value.toHours
    val seconds = value.getSeconds
    if (days == 0 && hours == 0) {
      "%d:%02d".format(seconds / 60, seconds % 60)
    } else if (days == 0) {
      "%d:%02d:%02d".format(seconds / 3600, (seconds % 3600) / 60, seconds % 60)
    } else {
      "%dd %02d:%02d:%02d".format(seconds / (3600 * 24), (seconds / 3600) % 24, (seconds % 3600) / 60, seconds % 60)
    }
  }
  override def parser = DurationParser
}

case object HourDurationRenderer extends SpecialHandlingDurationRenderer {
  private val format = new DecimalFormat("0.00")
  override def renderDuration(value:Duration, locale:Locale) = format.format(value.getSeconds / 3600.0)
  override def parser = ???
}

trait SpecialHandlingDurationRenderer extends Renderer[Duration] {
  private val specialDuration = Duration.ofSeconds(Long.MinValue)
  override final def render(value:Duration, locale:Locale) = {
    if (value == specialDuration) {
      ""
    } else {
      renderDuration(value, locale)
    }
  }
  def renderDuration(value:Duration, locale:Locale):String
}

class LocalDateRenderer(pattern:String="dd-MM-yyyy") extends Renderer[LocalDate] {
  override def render(value:LocalDate, locale:Locale) = {
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
    value.format(formatter)
  }
  override def parser = LocalDateParser
}

object LocalDateRenderer {
  def apply(pattern:String="dd-MM-yyyy") = new LocalDateRenderer(pattern)

  // Don't really need this as the LocalDateRenderer in the future will allow itself to be configured with different patterns
  val MonthYearRenderer = new LocalDateRenderer("MMM-yyyy") {
    override def id = super.id + ".MonthYearRenderer"
    override def name = "monthYearRenderer"
  }
}

class YearMonthRenderer(pattern:String="MMM-yyyy") extends Renderer[YearMonth] {
  override def render(value:YearMonth, locale:Locale) = {
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
    value.format(formatter)
  }
  override def parser = YearMonthParser
}

object YearMonthRenderer {
  def apply(pattern:String="MMM-yyyy") = new YearMonthRenderer(pattern)
}