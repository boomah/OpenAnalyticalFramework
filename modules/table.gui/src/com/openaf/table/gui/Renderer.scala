package com.openaf.table.gui

import java.util.Locale

import com.openaf.table.lib.api.StandardFields._
import com.openaf.table.lib.api._
import java.lang.{Double => JDouble}

trait Renderer[V] {
  /**
   * Unique identifier for the renderer. Is used in the TableState to indicate which renderer is in use.
   */
  def id:RendererId.RendererId = getClass.getName

  /**
   * The text used to look up the name of the renderer in the resource file. If there is no entry in the resource file,
   * this text is displayed.
   */
  def name:String = {
    val className = getClass.getSimpleName.replace("$", "")
    if (className.length > 1) className.head.toLower + className.tail else className.toLowerCase
  }
  def render(value:V, locale:Locale):String
  def parser:Parser[V]
}

case object BlankRenderer extends Renderer[Any] {
  override def render(value:Any, locale:Locale) = ""
  override def parser = AnyParser
}

case class NoValueAwareDelegatingRenderer[V](renderer:Renderer[V]) extends Renderer[Any] {
  override def id = renderer.id
  override def name = renderer.name
  override def render(value:Any, locale:Locale) = if (value == NoValue) "" else renderer.render(value.asInstanceOf[V], locale)
  override def parser = renderer.parser.asInstanceOf[Parser[Any]]
}

case object AnyRenderer extends Renderer[Any] {
  override def render(value:Any, locale:Locale) = value.toString
  override def parser = AnyParser
}

case object StringRenderer extends Renderer[String] {
  override def render(value:String, locale:Locale) = value
  override def parser = StringParser
}

case object IntRenderer extends Renderer[Int] {
  override def render(value:Int, locale:Locale) = value.toString
  override def parser = IntParser
}

case class FormattedIntRenderer(format:String="%05d") extends Renderer[Int] {
  override def render(value:Int, locale:Locale) = format.format(value)
  override def parser = IntParser
}

case object IntegerRenderer extends Renderer[Integer] {
  override def render(value:Integer, locale:Locale) = value.toString
  override def parser = IntegerParser
}

case class FormattedIntegerRenderer(format:String="%05d") extends Renderer[Integer] {
  override def render(value:Integer, locale:Locale) = format.format(value)
  override def parser = IntegerParser
}

case class JDoubleRenderer(format:String="%.4f") extends Renderer[JDouble] {
  override def render(value:JDouble, locale:Locale) = format.format(value)
  override def parser = JDoubleParser
}

case object DefaultRenderer extends Renderer[Any] {
  override def id = RendererId.DefaultRendererId
  override def render(value:Any, locale:Locale) = {
    if (value == null) {
      "null value - should never happen"
    } else {
      value match {
        case NoValue => ""
        case other => other.toString
      }
    }
  }
  override def parser = AnyParser
}

object MSetRenderer {
  val DefaultMaxNumberToDisplay = 3
}

object Renderer {
  val StandardRenderers:Map[FieldID,Map[TransformerType[_],List[Renderer[_]]]] = Map(
    CountField.id -> Map(
      IdentityTransformerType -> List(IntRenderer)
    )
  )

  val IntRenderers:Map[TransformerType[_],List[Renderer[_]]] = Map(
    IdentityTransformerType -> List(IntRenderer)
  )

  val IntegerRenderers:Map[TransformerType[_],List[Renderer[_]]] = Map(
    IdentityTransformerType -> List(IntegerRenderer, FormattedIntegerRenderer()),
    IntegerToJDoubleTransformerType -> List(JDoubleRenderer())
  )

  val StringRenderers:Map[TransformerType[_],List[Renderer[_]]] = Map(
    IdentityTransformerType -> List(StringRenderer)
  )

  val LocalDateRenderers:Map[TransformerType[_],List[Renderer[_]]] = Map(
    IdentityTransformerType -> List(LocalDateRenderer(), LocalDateRenderer.MonthYearRenderer),
    LocalDateToYearMonthTransformerType -> List(YearMonthRenderer())
  )

  val DurationRenderers:Map[TransformerType[_],List[Renderer[_]]] = Map(
    IdentityTransformerType -> List(DurationRenderer, HourDurationRenderer)
  )
}

class Renderers(renderers:Map[FieldID,Map[TransformerType[_],List[Renderer[_]]]]=Map.empty) {
  private val idToRenderers:Map[RendererId.RendererId,Renderer[_]] = renderers.flatMap{case (_,transformerTypeToRenderer) =>
    transformerTypeToRenderer.flatMap{case (_,rendererList) => rendererList.map(renderer => renderer.id -> renderer)}
  }.toMap
  def renderer(field:Field[_]):Renderer[_] = {
    val selectedRenderer = if (field.rendererId == RendererId.DefaultRendererId) {
      renderers.get(field.id).flatMap(_.get(field.transformerType)).flatMap(_.headOption).getOrElse(DefaultRenderer)
    } else {
      idToRenderers.getOrElse(field.rendererId, DefaultRenderer)
    }
    NoValueAwareDelegatingRenderer(selectedRenderer)
  }
  def renderers(fieldId:FieldID, transformerType:TransformerType[_]):List[Renderer[_]] = {
    renderers.get(fieldId).flatMap(_.get(transformerType)).getOrElse(Nil)
  }
}
