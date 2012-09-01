package com.openaf.browser.gui

import javafx.scene.image.Image
import ref.SoftReference

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

case class PageInfo(page:Page, softPageResponse:SoftReference[PageResponse])

trait PageFactory {
  def page:Page
}