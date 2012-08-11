package com.openaf.rmi.server

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import java.util.concurrent.{ConcurrentHashMap, Executors}
import org.jboss.netty.bootstrap.ServerBootstrap
import java.net.InetSocketAddress
import org.jboss.netty.channel.Channel
import com.openaf.rmi.common.{DefaultObjectEncoder, ServicesListing, MethodInvocationResult, MethodInvocationRequest}

class RMIServer(port:Int) {
  private val services = new ConcurrentHashMap[String,AnyRef]()
  private val serviceListings = new ServerServicesListings(services)
  addService(classOf[ServicesListing].getName, serviceListings)
  private val serverFunctions = new ServerFunctions(services)
  private val factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool())
  private val bootstrap = new ServerBootstrap(factory)
  bootstrap.setPipelineFactory(new ServerPipelineFactory(serverFunctions))
  bootstrap.setOption("child.tcpNoDelay", true)
  bootstrap.setOption("child.keepAlive", true)

  private var channel:Channel = _

  def start() {
    channel = bootstrap.bind(new InetSocketAddress(port))
  }

  def stop() {
    Option(channel).foreach(chan => {chan.close.await})
    bootstrap.releaseExternalResources()
  }

  def addService(className:String, service:AnyRef) {services.put(className, service)}
  def removeService(className:String) {services.remove(className)}
}

class ServerFunctions(services:ConcurrentHashMap[String,AnyRef]) {
  def invokeMethod(request:MethodInvocationRequest) = {
    val service = services.get(request.className)
    val serviceClass = service.getClass
    val serviceClassLoader = serviceClass.getClassLoader
    val paramsClasses = request.params.map(param => loadClass(param, serviceClassLoader))
    val serviceMethod = serviceClass.getMethod(request.methodName, paramsClasses :_*)
    val paramsValues = request.paramValuesBytes.map(bytes => DefaultObjectEncoder.decode(bytes, serviceClassLoader))
    val serviceResult = serviceMethod.invoke(service, paramsValues :_*)
    val serviceResultBytes = DefaultObjectEncoder.encode(serviceResult)
    MethodInvocationResult(request.id, serviceResultBytes)
  }

  private def loadClass(className:String, classLoader:ClassLoader) = {
    className match {
      case "boolean" => java.lang.Boolean.TYPE
      case "int" => java.lang.Integer.TYPE
      case "long" => java.lang.Long.TYPE
      case "float" => java.lang.Float.TYPE
      case "double" => java.lang.Double.TYPE
      case "byte" => java.lang.Byte.TYPE
      case "char" => java.lang.Character.TYPE
      case "short" => java.lang.Short.TYPE
      case _ => classLoader.loadClass(className)
    }
  }
}

class ServerServicesListings(servicesMap:ConcurrentHashMap[String,_]) extends ServicesListing {
  import scala.collection.JavaConversions._
  def services = servicesMap.keySet.toList
}