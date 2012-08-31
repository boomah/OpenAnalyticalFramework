package com.openaf.browser.animation

import javafx.animation._
import javafx.util.Duration
import javafx.scene.layout.StackPane
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.image.ImageView

trait BrowserPageAnimation {
  def animate(content:StackPane, fromPageComponent:ImageView, toPageComponent:ImageView, onComplete: => Unit)
}

object BrowserPageAnimation {
  val PageSlideTime = Duration.millis(250.0)
  val NoAnimation = new BrowserPageAnimation {
    def animate(content:StackPane, fromPageComponent:ImageView, toPageComponent:ImageView, onComplete: => Unit) {onComplete}
  }
}
import BrowserPageAnimation._

object ForwardOnePageTransition extends BrowserPageAnimation {
  def animate(content:StackPane, fromPageComponent:ImageView, toPageComponent:ImageView, onComplete: => Unit) {
    toPageComponent.setTranslateX(toPageComponent.getImage.getWidth)
    content.getChildren.add(toPageComponent)

    val fromTransition = new TranslateTransition(PageSlideTime, fromPageComponent)
    fromTransition.setToX(-fromPageComponent.getImage.getWidth)
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
  def animate(content:StackPane, fromPageComponent:ImageView, toPageComponent:ImageView, onComplete: => Unit) {
    toPageComponent.setTranslateX(-toPageComponent.getImage.getWidth)
    content.getChildren.add(toPageComponent)

    val fromTransition = new TranslateTransition(PageSlideTime, fromPageComponent)
    fromTransition.setToX(fromPageComponent.getImage.getWidth)
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