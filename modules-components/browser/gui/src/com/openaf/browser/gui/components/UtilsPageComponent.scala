package com.openaf.browser.gui.components

import javafx.scene.layout.{VBox, FlowPane, BorderPane}
import com.openaf.browser.gui.pages.UtilsPage
import javafx.scene.control.{Button, Label}
import javafx.collections.{ObservableList, ListChangeListener}
import javafx.collections.ListChangeListener.Change
import scala.collection.JavaConversions._
import com.openaf.pagemanager.api.NoPageData
import com.openaf.browser.gui.utils.BrowserUtils
import com.openaf.browser.gui.api.{PageComponentFactory, OpenAFApplication, BrowserCacheKey}
import javafx.event.{ActionEvent, EventHandler}
import java.util.Locale
import com.openaf.browser.gui.InternalBrowserCacheKey
import com.openaf.browser.gui.binding.ApplicationLocaleStringBinding

class UtilsPageComponent extends BorderPane with BrowserPageComponent {
  type P = UtilsPage.type
  type PD = NoPageData.type
  def nameId = "utilsName"

  override def initialise() {
    val browserApplications = context.cache(InternalBrowserCacheKey.ApplicationsKey)
    browserApplications.addListener(new ListChangeListener[OpenAFApplication] {
      def onChanged(change:Change[_<:OpenAFApplication]) {updateBrowserApplications(browserApplications)}
    })
    updateBrowserApplications(browserApplications)
  }

  private val content = new VBox
  private val browserApplicationsPane = new FlowPane
  private def updateBrowserApplications(browserApplications:ObservableList[OpenAFApplication]) {
    val utilButtons = browserApplications.filter(_.utilButtons(context).nonEmpty).map(application => {
      val nameBinding = new ApplicationLocaleStringBinding(BrowserUtils.ApplicationName, application, context.cache)
      new OpenAFApplicationComponent(context, nameBinding, application.utilButtons(context))
    })
    browserApplicationsPane.getChildren.clear()
    browserApplicationsPane.getChildren.addAll(utilButtons.toArray :_*)
  }

  private val localePane = new FlowPane
  private val englishButton = new Button("English")
  englishButton.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {
    context.cache(BrowserCacheKey.LocaleKey).setValue(Locale.UK)
  }})
  private val frenchButton = new Button("French")
  frenchButton.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {
    context.cache(BrowserCacheKey.LocaleKey).setValue(Locale.FRANCE)
  }})
  localePane.getChildren.addAll(englishButton, frenchButton)

  content.getChildren.addAll(browserApplicationsPane, localePane)

  setTop(new Label("UTILS PAGE"))
  setCenter(content)

  def setup() {}
}

object UtilsPageComponentFactory extends PageComponentFactory {
  def pageComponent = new UtilsPageComponent
}
