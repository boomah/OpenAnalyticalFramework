package com.openaf.table.gui.binding

import javafx.beans.property.Property
import java.util.{ResourceBundle, Locale}
import javafx.beans.binding.StringBinding

import com.openaf.table.gui.Renderer

class TableLocaleStringBinding(id:String, localeProperty:Property[Locale],
                               prefix:Option[String]=None) extends StringBinding {
  bind(localeProperty)
  def computeValue = prefix.getOrElse("") + TableLocaleStringBinding.stringFromBundle(id, localeProperty.getValue)
}

object TableLocaleStringBinding {
  def stringFromBundle(id:String, locale:Locale):String = {
    val bundle = ResourceBundle.getBundle("com.openaf.table.gui.resources.table", locale)
    if (bundle.containsKey(id)) bundle.getString(id) else id
  }
}

object RendererNameBinding {
  val ModulePattern = """com\.openaf\.(.+)\.gui""".r
}

import RendererNameBinding._

class RendererNameBinding(renderer:Renderer[_], localeProperty:Property[Locale]) extends StringBinding {
  bind(localeProperty)
  override def computeValue = {
    val id = renderer.id
    val packageName = renderer.getClass.getPackage.getName
    packageName match {
      case ModulePattern(module) =>
        val locale = localeProperty.getValue
        val bundleLocation = packageName + ".resources." + module
        val bundle = ResourceBundle.getBundle(bundleLocation, locale, renderer.getClass.getClassLoader)
        if (bundle.containsKey(id)) bundle.getString(id) else id
      case _ => id
    }
  }
}