package com.openaf.start.server

import com.openaf.sport.SportPageDataFacilityImpl
import com.openaf.rmi.server.RMIServer
import com.openaf.sport.api.SportPageDataFacility

object Server {
  def main(args:Array[String]) {
    val server = new RMIServer(9654)
    val sportDataFacilityImpl = new SportPageDataFacilityImpl
    server.addService(classOf[SportPageDataFacility].getName, sportDataFacilityImpl)
    server.start()
    println("Server Started")
  }
}
