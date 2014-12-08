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
    println("Client Message Recieved " + message)
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

  def sendMessage(classToProxy:Class[_], method:Method, args:Array[AnyRef]) = {
    val id = requestID.getAndIncrement
    val requestDetails = RequestDetails(id, method, classToProxy.getClassLoader)
    requestDetailsMap.put(id, requestDetails)
    val paramValuesBytes = if (args != null) {
      args.map(arg => DefaultObjectEncoder.encode(arg))
    } else {
      Array[Array[Byte]]()
    }
    val request = MethodInvocationRequest(id, classToProxy.getName, method.getName,
      method.getParameterTypes.map(_.getName), paramValuesBytes)
    channel.write(request)
    requestDetails.waitForResult
  }
}

case class RequestDetails(id:Long, method:Method, classLoader:ClassLoader) {
  private val lock = new Object
  private var result0:AnyRef = null // Only ever access in lock.synchronized block
  def result = lock.synchronized{result0}
  def result_=(r:AnyRef) {
    lock.synchronized {
      assert((result0 == null), "Result can only be set once. Current/New: " + (result0, r))
      result0 = r
      lock.notifyAll()
    }
  }
  def waitForResult = {
    lock.synchronized {
      if (result0 == null) {
        lock.wait()
        result0
      } else {
        result0
      }
    }
  }
}