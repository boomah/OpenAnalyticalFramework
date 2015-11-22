package com.openaf.sport.gui

import com.openaf.sport.api.SportPage._
import com.openaf.table.lib.api.TableState
import org.osgi.framework.{BundleContext, BundleActivator}
import com.openaf.browser.gui.api._
import com.openaf.table.gui._
import com.openaf.sport.gui.components.{RunningPageComponentFactory, GoalsPageComponentFactory}
import com.openaf.sport.api.{RunningPage, GoalsPage}

class SportBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("SportBundleActivator gui started")
    context.registerService(classOf[OpenAFApplication], SportBrowserApplication, null)
  }
  def stop(context:BundleContext) {
    println("SportBundleActivator gui stopped")
  }
}

object SportBrowserApplication extends OpenAFApplication {
  private val styleSheet = getClass.getResource("/com/openaf/sport/gui/resources/sport.css").toExternalForm

  override def applicationButtons(context:BrowserContext) = {
    List(
      BrowserActionButton(GoalsPageComponentFactory.pageComponent.nameId, GoalsPageFactory),
      BrowserActionButton(RunningPageComponentFactory.pageComponent.nameId, RunningPageFactory)
    )
  }
  override def componentFactoryMap:Map[String,PageComponentFactory] = Map(
    classOf[GoalsPage].getName -> GoalsPageComponentFactory,
    classOf[RunningPage].getName -> RunningPageComponentFactory
  )
  override def styleSheets = styleSheet :: OpenAFTable.styleSheets
  override def order = -1 // For now I want the sport application to be first
}

object GoalsPageFactory extends PageFactory {
  override def page = GoalsPage(TableState.Blank)
}

object RunningPageFactory extends PageFactory {
  override def page = RunningPage(TableState.Blank)
}

object SportRenderers {
  val GoalPageRenderers = Map(
    PlayerField.id -> Renderer.StringRenderers,
    StartTimeField.id -> Renderer.IntRenderers,
    TeamField.id -> Renderer.StringRenderers,
    OppositionTeamField.id -> Renderer.StringRenderers,
    VenueField.id -> Renderer.StringRenderers,
    DateField.id -> Renderer.StringRenderers,
    KickOffTimeField.id -> Renderer.StringRenderers,
    CompetitionField.id -> Renderer.StringRenderers
  )

  val RunningPageRenderers = Map(
    LocationField.id -> Renderer.StringRenderers,
    NumberField.id -> Renderer.IntegerRenderers,
    DateField.id -> Renderer.LocalDateRenderers,
    PositionField.id -> Renderer.IntegerRenderers,
    TimeField.id -> Renderer.DurationRenderers,
    AgeCatField.id -> Renderer.StringRenderers,
    AgeGradeField.id -> Renderer.StringRenderers,
    GenderField.id -> Renderer.StringRenderers,
    GenderPosField.id -> Renderer.IntegerRenderers,
    ClubField.id -> Renderer.StringRenderers,
    NoteField.id -> Renderer.StringRenderers
  )
}