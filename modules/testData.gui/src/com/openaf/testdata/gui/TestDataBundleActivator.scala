package com.openaf.testdata.gui

import java.util.Locale

import com.openaf.browser.gui.api.{PageFactory, PageComponentFactory, BrowserActionButton, BrowserContext}
import com.openaf.table.gui.{Renderer, FormattedIntRenderer, IntRenderer}
import com.openaf.table.gui.api.{TablePageComponentFactory, TableApplication, TableBundleActivator}
import com.openaf.table.lib.api.TableState
import com.openaf.testdata.api.{TestDataPage, StringWrapper}
import com.openaf.testdata.api.TestDataTablePageDataFacility._

class TestDataBundleActivator extends TableBundleActivator {
  override def tableApplication = TestDataTableApplication
}

object TestDataTableApplication extends TableApplication {
  override def nameId = NameId
  override def defaultRenderers = Map(
    IdField.id -> IntRenderer,
    PersonField.id -> StringWrapperRenderer
  )
  override def additionalRenderers = Map(
    IdField.id -> List(FormattedIntRenderer())
  )
  override def componentFactoryMap = Map(
    classOf[TestDataPage].getName -> new TablePageComponentFactory(nameId, defaultRenderers, additionalRenderers)
  )
  override def applicationButtons(context:BrowserContext) = {
    List(BrowserActionButton(nameId, TestDataPageFactory))
  }
}

case object StringWrapperRenderer extends Renderer[StringWrapper] {
  override def render(value:StringWrapper, locale:Locale) = value.string
}

object TestDataPageFactory extends PageFactory {
  def page = TestDataPage(TableState.Blank)
}
