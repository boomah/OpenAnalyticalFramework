package com.openaf.pagemanager.api

trait PageManagerFacility {
  def pageData(page:Page):PageData
}

trait Page {
  def pageDataFacility(sc:ServerContext):PageDataFacility
}

trait PageData

case object NoPageData extends PageData

trait PageFactory {
  def page:Page
}

trait ServerContext {
  def facility[T](klass:Class[T]):T
}