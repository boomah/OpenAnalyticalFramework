package com.openaf.properties.api

trait PropertiesService {
  def name:String
  def hostName:String
  def webPort:Int
  def servicesPort:Int
  def webExternalURL = "http://" + hostName + ":" + webPort.toString
}
