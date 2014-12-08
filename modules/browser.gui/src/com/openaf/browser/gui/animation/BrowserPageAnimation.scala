package com.openaf.browser.gui.animation

import javafx.animation._
import javafx.util.Duration
import javafx.scene.layout.Region
import javafx.event.{ActionEvent, EventHandler}

trait BrowserPageAnimation {
  def animate(fromPageComponent:Region, toPageComponent:Region, onComplete: => Unit)
}

object BrowserPageAnimation {
  val PageSlideTime = Duration.millis(250.0)
  val NoAnimation = new BrowserPageAnimation {
    def animate(fromPageComponent:Region, toPageComponent:Region, onComplete: => Unit) {onComplete}
  }
}
import BrowserPageAnimation._

object ForwardOnePageTransition extends BrowserPageAnimation {
  def animate(fromPageComponent:Region, toPageComponent:Region, onComplete: => Unit) {
    toPageComponent.setTranslateX(fromPageComponent.getWidth / 3)

    val fromTransition = new TranslateTransition(PageSlideTime, fromPageComponent)
    fromTransition.setToX(-fromPageComponent.getWidth)
    fromTransition.setInterpolator(Interpolator.EASE_BOTH)

    val toTransition = new TranslateTransition(PageSlideTime, toPageComponent)
    toTransition.setToX(0.0)
    toTransition.setInterpolator(Interpolator.EASE_BOTH)
    toTransition.setOnFinished(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {onComplete}
    })

    fromTransition.playFromStart()
    toTransition.playFromStart()
  }
}

object BackOnePageTransition extends BrowserPageAnimation {
  def animate(fromPageComponent:Region, toPageComponent:Region, onComplete: => Unit) {
    toPageComponent.setTranslateX(-fromPageComponent.getWidth / 3)

    val fromTransition = new TranslateTransition(PageSlideTime, fromPageComponent)
    fromTransition.setToX(fromPageComponent.getWidth)
    fromTransition.setInterpolator(Interpolator.EASE_BOTH)

    val toTransition = new TranslateTransition(PageSlideTime, toPageComponent)
    toTransition.setToX(0.0)
    toTransition.setInterpolator(Interpolator.EASE_BOTH)
    toTransition.setOnFinished(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {onComplete}
    })

    fromTransition.playFromStart()
    toTransition.playFromStart()
  }
}