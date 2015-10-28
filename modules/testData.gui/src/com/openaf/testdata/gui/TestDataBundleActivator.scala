package com.openaf.testdata.gui

import java.util.Locale

import com.openaf.browser.gui.api.{PageFactory, BrowserActionButton, BrowserContext}
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
    IdField.id -> List(IntRenderer, FormattedIntRenderer()),
    PersonField.id -> List(StringWrapperRenderer),
    ScoreField.id -> List(IntRenderer)
  )
  override def componentFactoryMap = Map(
    classOf[TestDataPage].getName -> new TablePageComponentFactory(nameId, defaultRenderers)
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
