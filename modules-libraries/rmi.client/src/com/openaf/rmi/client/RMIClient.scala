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

  def connect = {
    bootstrap.connect(new InetSocketAddress(hostName, port))
  }

  def connectBlocking() {
    connect.awaitUninterruptibly()
  }

  def proxy[T](proxyClass:Class[T]):T = {
    JProxy.newProxyInstance(proxyClass.getClassLoader, Array(proxyClass), new InvocationHandler {
      def invoke(proxy:Any, method:Method, args:Array[AnyRef]) = {
        clientHandler.sendMessage(method, args)
      }
    }).asInstanceOf[T]
  }
}
