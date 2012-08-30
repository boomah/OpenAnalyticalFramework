package com.openaf.browser.animation

import javafx.animation.{Interpolator, TranslateTransition}
import javafx.util.Duration
import javafx.scene.layout.{StackPane, HBox}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.image.ImageView

trait BrowserPageAnimation {
  def animate(content:StackPane, fromPageComponent:ImageView, toPageComponent:ImageView, onComplete: => Unit)
}

object BrowserPageAnimation {
  val PageSlideTime = new Duration(250.0)
  val NoAnimation = new BrowserPageAnimation {
    def animate(content:StackPane, fromPageComponent:ImageView, toPageComponent:ImageView, onComplete: => Unit) {onComplete}
  }
}
import BrowserPageAnimation._

object ForwardOnePageTransition extends BrowserPageAnimation {
  def animate(content:StackPane, fromPageComponent:ImageView, toPageComponent:ImageView, onComplete: => Unit) {
    val combinedNode = new HBox
    combinedNode.getChildren.addAll(fromPageComponent, toPageComponent)
    content.getChildren.add(combinedNode)
    val transition = new TranslateTransition(PageSlideTime, combinedNode)
    transition.setToX(-fromPageComponent.getImage.getWidth)
    transition.setInterpolator(Interpolator.EASE_BOTH)
    transition.setOnFinished(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {onComplete}
    })
    transition.playFromStart()
  }
}

object BackOnePageTransition extends BrowserPageAnimation {
  def animate(content:StackPane, fromPageComponent:ImageView, toPageComponent:ImageView, onComplete: => Unit) {
    val combinedNode = new HBox
    combinedNode.getChildren.addAll(toPageComponent, fromPageComponent)
    combinedNode.setTranslateX(-fromPageComponent.getImage.getWidth)
    content.getChildren.add(combinedNode)
    val transition = new TranslateTransition(PageSlideTime, combinedNode)
    transition.setToX(0)
    transition.setInterpolator(Interpolator.EASE_BOTH)
    transition.setOnFinished(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {onComplete}
    })
    transition.playFromStart()
  }
}