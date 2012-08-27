package com.openaf.browser

import javafx.scene.image.Image

trait Page {
  def name:String
  def image:Image
  def build:PageData
}

trait PageData

object PageData {
  val NoPageData = new NoPageData
}

class NoPageData extends PageData

case class PageInfo(page:Page)

trait PageFactory {
  def page:Page
}