package com.openaf.table.gui

import java.util.Locale

import com.openaf.table.lib.api._
import com.openaf.table.lib.api.StandardFields._

trait Renderer[V] {
  def id:RendererId.RendererId = getClass.getName
  def name:String = {
    val className = getClass.getSimpleName.replace("$", "")
    if (className.length > 1) className.head.toLower + className.tail else className.toLowerCase
  }
  def render(value:V, locale:Locale):String
}

case object BlankRenderer extends Renderer[Any] {
  def render(value:Any, locale:Locale) = ""
}

case class NoValueAwareDelegatingRenderer[V](renderer:Renderer[V]) extends Renderer[Any] {
  override def id = renderer.id
  override def name = renderer.name
  def render(value:Any, locale:Locale) = if (value == NoValue) "" else renderer.render(value.asInstanceOf[V], locale)
}

case object AnyRenderer extends Renderer[Any] {
  def render(value:Any, locale:Locale) = value.toString
}

case object StringRenderer extends Renderer[String] {
  def render(value:String, locale:Locale) = value
}

case object IntRenderer extends Renderer[Int] {
  def render(value:Int, locale:Locale) = value.toString
}

case class FormattedIntRenderer(format:String="%05d") extends Renderer[Int] {
  override def render(value:Int, locale:Locale) = format.format(value)
}

case object IntegerRenderer extends Renderer[Integer] {
  def render(value:Integer, locale:Locale) = value.toString
}

case object MutIntRenderer extends Renderer[MutInt] {
  def render(value:MutInt, locale:Locale) = value.value.toString
}

case object DefaultRenderer extends Renderer[Any] {
  override def id = RendererId.DefaultRendererId
  override def render(value:Any, locale:Locale) = {
    if (value == null) {
      "null value - should never happen"
    } else {
      value match {
        case NoValue => ""
        case mutInt:MutInt => MutIntRenderer.render(mutInt, locale)
        case other => other.toString
      }
    }
  }
}

object MSetRenderer {
  val DefaultMaxNumberToDisplay = 3
}

object Renderer {
  val StandardRenderers:Map[FieldID,List[Renderer[_]]] = Map(
    CountField.id -> List(MutIntRenderer)
  )
}

class Renderers(renderers:Map[FieldID,List[Renderer[_]]]=Map.empty) {
  private val idToRenderers:Map[RendererId.RendererId,Renderer[_]] = renderers.flatMap{case (_,rendererList) =>
      rendererList.map(renderer => renderer.id -> renderer)
  }.toMap
  def renderer(field:Field[_]):Renderer[_] = {
    val selectedRenderer = if (field.rendererId == RendererId.DefaultRendererId) {
      renderers.get(field.id).flatMap(_.headOption).getOrElse(DefaultRenderer)
    } else {
      idToRenderers.getOrElse(field.rendererId, DefaultRenderer)
    }
    NoValueAwareDelegatingRenderer(selectedRenderer)
  }
  def renderers(fieldId:FieldID):List[Renderer[_]] = renderers.getOrElse(fieldId, Nil)
}
