package com.openaf.table.gui.api

import com.openaf.browser.gui.api._
import com.openaf.table.api.StandardTablePage
import com.openaf.table.gui.{Renderer, OpenAFTable}
import com.openaf.table.lib.api.{TransformerType, FieldID, TableState}
import org.osgi.framework.{BundleActivator, BundleContext}

trait TableApplication extends OpenAFApplication {
  def nameId:String
  def defaultRenderers:Map[FieldID,Map[TransformerType[_],List[Renderer[_]]]] = Map.empty
  private def tablePageFactory = new TablePageFactory(nameId)
  override def applicationButtons(context:BrowserContext) = List(BrowserActionButton(nameId, tablePageFactory))
  override def componentFactoryMap = Map(
    classOf[StandardTablePage].getName -> new TablePageComponentFactory(nameId, defaultRenderers)
  )
  override def styleSheets = OpenAFTable.styleSheets
}

class TablePageFactory(nameId:String) extends PageFactory {
  def page = StandardTablePage(nameId, TableState.Blank)
}

trait TableBundleActivator extends BundleActivator {
  def tableApplication:TableApplication
  override def start(context:BundleContext) = {
    println(s"${tableApplication.nameId} gui started")
    context.registerService(classOf[OpenAFApplication], tableApplication, null)
  }
  override def stop(context:BundleContext) = {
    println(s"${tableApplication.nameId} gui stopped")
  }
}

class TablePageComponentFactory(nameId:String, defaultRenderers:Map[FieldID,Map[TransformerType[_],List[Renderer[_]]]]) extends PageComponentFactory {
  def pageComponent = new StandardTablePageComponent(nameId, defaultRenderers)
}