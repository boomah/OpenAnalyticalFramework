package com.openaf.table.gui

import java.util.Locale

import com.openaf.table.lib.api.{Field, NoValue, MutInt, FieldID}
import com.openaf.table.lib.api.StandardFields._

trait Renderer[V] {
  def id:String = {
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
  val StandardRenderers:Map[FieldID,Renderer[_]] = Map(
    CountField.id -> MutIntRenderer
  )
}

class Renderers(defaultRenderers:Map[FieldID,Renderer[_]], additionalRenderers:Map[FieldID,List[Renderer[_]]]=Map.empty,
                selectedRenderers:Map[Field[_],Renderer[_]]=Map.empty) {
  def renderer(field:Field[_]):Renderer[_] = NoValueAwareDelegatingRenderer(selectedRenderer(field))

  def selectedRenderer(field:Field[_]):Renderer[_] = {
    selectedRenderers.getOrElse(field, defaultRenderers.getOrElse(field.id, DefaultRenderer))
  }

  def updateSelectedRenderer(field:Field[_], renderer:Renderer[_]):Renderers = {
    require(renderers(field.id).contains(renderer))
    val updatedSelectedRenderers = selectedRenderers + (field -> renderer)
    new Renderers(defaultRenderers, additionalRenderers, updatedSelectedRenderers)
  }

  def renderers(fieldId:FieldID):List[Renderer[_]] = {
    (defaultRenderers.get(fieldId) ++ additionalRenderers.getOrElse(fieldId, Nil)).toList
  }
}
