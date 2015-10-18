package com.openaf.pagemanager.api

trait PageManagerFacility {
  def pageData(page:Page):PageData
}

trait Page {
  def nameId:String = getClass.getSimpleName
  def pageDataFacility(sc:ServerContext):PageDataFacility
}

trait PageData

case object NoPageData extends PageData
case class ExceptionPageData(exception:Exception) extends PageData

trait ServerContext {
  def facilities[T](klass:Class[T]):List[T]
  def facility[T](klass:Class[T]):T = facilities(klass).head
}