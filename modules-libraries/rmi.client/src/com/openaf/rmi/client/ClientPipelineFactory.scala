package com.openaf.rmi.client

import com.openaf.rmi.common.{DefaultObjectEncoder, MethodInvocationResult, MethodInvocationRequest, RMICommon}
import org.jboss.netty.channel._
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class ClientPipelineFactory(clientHandler:ClientHandler) extends ChannelPipelineFactory {
  def getPipeline = {
    val pipeline = Channels.pipeline
    RMICommon.addHandlers(pipeline, getClass.getClassLoader)
    pipeline.addLast("clientHandler", clientHandler)
    pipeline
  }
}

class ClientHandler extends SimpleChannelHandler {
  private var channel:Channel = _
  private val requestDetailsMap = new ConcurrentHashMap[Long,RequestDetails]
  private val requestID = new AtomicLong

  override def channelConnected(ctx:ChannelHandlerContext, e:ChannelStateEvent) {
    println("Channel Connected")
    channel = ctx.getChannel
  }

  override def channelDisconnected(ctx:ChannelHandlerContext, e:ChannelStateEvent) {
    println("Channel Disconnected")
  }

  override def exceptionCaught(ctx:ChannelHandlerContext, e:ExceptionEvent) {
    e.getCause.printStackTrace()
  }

  override def messageReceived(ctx:ChannelHandlerContext, e:MessageEvent) {
    val message = e.getMessage
    println("Message Recieved " + message)
    message match {
      case MethodInvocationResult(id, resultBytes) => {
        val requestDetails = requestDetailsMap.get(id)
        val classLoader = requestDetails.classLoader
        val result = DefaultObjectEncoder.decode(resultBytes, classLoader)
        requestDetails.result = result
      }
      case _ => println("Don't know how to handle " + message)
    }
  }

  def sendMessage(proxyClass:Class[_], method:Method, args:Array[AnyRef]) = {
    val id = requestID.getAndIncrement
    val requestDetails = RequestDetails(id, method, proxyClass.getClassLoader)
    requestDetailsMap.put(id, requestDetails)
    val paramValuesBytes = if (args != null) {
      args.map(arg => DefaultObjectEncoder.encode(arg))
    } else {
      Array[Array[Byte]]()
    }
    val request = MethodInvocationRequest(id, proxyClass.getName, method.getName,
      method.getParameterTypes.map(_.getName), paramValuesBytes)
    channel.write(request)
    requestDetails.waitForResult
  }
}

case class RequestDetails(id:Long, method:Method, classLoader:ClassLoader) {
  private val lock = new Object
  private var result0:AnyRef = _
  def result = result0
  def result_=(r:AnyRef) {
    lock.synchronized {
      result0 = r
      lock.notifyAll()
    }
  }
  def waitForResult = {
    lock.synchronized {
      lock.wait()
      result
    }
  }
}