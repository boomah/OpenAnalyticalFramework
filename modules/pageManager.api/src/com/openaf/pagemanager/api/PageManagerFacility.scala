package com.openaf.pagemanager.api

trait PageManagerFacility {
  def pageData(page:Page):PageData
}

trait Page {
  def pageDataFacility(sc:ServerContext):PageDataFacility
}

trait PageData

case object NoPageData extends PageData
case class ExceptionPageData(exception:Exception) extends PageData

trait ServerContext {
  def facility[T](klass:Class[T]):T
}