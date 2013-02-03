package com.openaf.rmi.client

import org.jboss.netty.bootstrap.ClientBootstrap
import java.util.concurrent.Executors
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
import java.net.InetSocketAddress
import java.lang.reflect.{Proxy => JProxy, Method, InvocationHandler}

class RMIClient(hostName:String, port:Int) {
  private val factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool())
  private val bootstrap = new ClientBootstrap(factory)
  private val clientHandler = new ClientHandler
  bootstrap.setPipelineFactory(new ClientPipelineFactory(clientHandler))
  bootstrap.setOption("tcpNoDelay", true)
  bootstrap.setOption("keepAlive", true)
  private val objectMethods = classOf[Object].getMethods.map(_.getName).toSet

  def connect = {bootstrap.connect(new InetSocketAddress(hostName, port))}
  def connectBlocking() {connect.awaitUninterruptibly()}

  def proxy[T](classToProxy:Class[T]):T = {
    JProxy.newProxyInstance(classToProxy.getClassLoader, Array(classToProxy), new InvocationHandler {
      def invoke(proxy:Any, method:Method, args:Array[AnyRef]) = {
        assert(!objectMethods.contains(method.getName),
          "It is unlikely correct to call an Object method across the wire: " + classToProxy.getName + "." + method.getName)
        clientHandler.sendMessage(classToProxy, method, args)
      }
    }).asInstanceOf[T]
  }
}
