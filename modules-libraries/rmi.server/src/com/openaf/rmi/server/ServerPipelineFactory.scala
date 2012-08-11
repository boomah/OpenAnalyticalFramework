package com.openaf.rmi.server

import com.openaf.rmi.common.{MethodInvocationRequest, RMICommon}
import org.jboss.netty.channel._

class ServerPipelineFactory(serverFunctions:ServerFunctions) extends ChannelPipelineFactory {
  def getPipeline = {
    val pipeline = Channels.pipeline
    RMICommon.addHandlers(pipeline, getClass.getClassLoader)
    pipeline.addLast("serverHandler", new ServerHandler(serverFunctions))
    pipeline
  }
}

class ServerHandler(serverFunctions:ServerFunctions) extends SimpleChannelUpstreamHandler {
  override def channelConnected(ctx:ChannelHandlerContext, e:ChannelStateEvent) {
    println("Channel Connected")
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
      case methodInvocationRequest:MethodInvocationRequest => {
        val result = serverFunctions.invokeMethod(methodInvocationRequest)
        e.getChannel.write(result)
      }
    }
  }
}
