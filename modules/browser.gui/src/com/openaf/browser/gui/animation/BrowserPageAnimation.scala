package com.openaf.browser.gui.animation

import javafx.animation._
import javafx.util.Duration
import javafx.scene.layout.Region
import javafx.event.{ActionEvent, EventHandler}

trait BrowserPageAnimation {
  def animate(fromPageComponent:Region, toPageComponent:Region, onComplete: =>Unit)
}

object BrowserPageAnimation {
  val PageAnimationDuration = Duration.millis(250.0)
  val NoAnimation = new BrowserPageAnimation {
    def animate(fromPageComponent:Region, toPageComponent:Region, onComplete: =>Unit) {onComplete}
  }

  def animatePageTransition(direction:Int, fromPageComponent:Region, toPageComponent:Region, onComplete: =>Unit) {
    toPageComponent.setTranslateX(direction * fromPageComponent.getWidth / 3)

    val fromTransition = new TranslateTransition(PageAnimationDuration, fromPageComponent)
    fromTransition.setToX(direction * -fromPageComponent.getWidth)
    fromTransition.setInterpolator(Interpolator.EASE_BOTH)

    val toTransition = new TranslateTransition(PageAnimationDuration, toPageComponent)
    toTransition.setToX(0.0)
    toTransition.setInterpolator(Interpolator.EASE_BOTH)
    toTransition.setOnFinished(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {onComplete}
    })

    val fadeTransition = new FadeTransition(PageAnimationDuration, fromPageComponent)
    fadeTransition.setFromValue(1.0)
    fadeTransition.setToValue(0.0)
    fadeTransition.setInterpolator(Interpolator.EASE_BOTH)
    fadeTransition.setOnFinished(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {fromPageComponent.setOpacity(1.0)}
    })

    new ParallelTransition(fromTransition, toTransition, fadeTransition).playFromStart()
  }
}
import BrowserPageAnimation._

object ForwardOnePageTransition extends BrowserPageAnimation {
  def animate(fromPageComponent:Region, toPageComponent:Region, onComplete: =>Unit) {
    animatePageTransition(1, fromPageComponent, toPageComponent, onComplete)
  }
}

object BackOnePageTransition extends BrowserPageAnimation {
  def animate(fromPageComponent:Region, toPageComponent:Region, onComplete: =>Unit) {
    animatePageTransition(-1, fromPageComponent, toPageComponent, onComplete)
  }
}