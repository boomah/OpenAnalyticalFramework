package com.openaf.test.api

trait TestService {
  def message:String
  def message2(text:String):String
  def message3(say:Boolean, text:String):String
}
