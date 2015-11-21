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
    PlayerField.id -> List(StringRenderer),
    StartTimeField.id -> List(IntRenderer),
    TeamField.id -> List(StringRenderer),
    OppositionTeamField.id -> List(StringRenderer),
    VenueField.id -> List(StringRenderer),
    DateField.id -> List(StringRenderer),
    KickOffTimeField.id -> List(StringRenderer),
    CompetitionField.id -> List(StringRenderer)
  )

  val RunningPageRenderers = Map(
    LocationField.id -> List(StringRenderer),
    NumberField.id -> List(IntegerRenderer, FormattedIntegerRenderer()),
    DateField.id -> List(LocalDateRenderer(), LocalDateRenderer.MonthYearRenderer),
    PositionField.id -> List(IntegerRenderer, FormattedIntegerRenderer()),
    TimeField.id -> List(DurationRenderer, HourDurationRenderer),
    AgeCatField.id -> List(StringRenderer),
    AgeGradeField.id -> List(StringRenderer),
    GenderField.id -> List(StringRenderer),
    GenderPosField.id -> List(IntegerRenderer, FormattedIntegerRenderer()),
    ClubField.id -> List(StringRenderer),
    NoteField.id -> List(StringRenderer)
  )
}