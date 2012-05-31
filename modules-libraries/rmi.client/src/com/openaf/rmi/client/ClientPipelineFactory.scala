package com.openaf.rmi.client

import com.openaf.rmi.common.RMICommon
import org.jboss.netty.channel._

class ClientPipelineFactory extends ChannelPipelineFactory {
  def getPipeline = {
    val pipeline = Channels.pipeline
    RMICommon.addHandlers(pipeline, getClass.getClassLoader)
    pipeline.addLast("clientHandler", new ClientHandler)
    pipeline
  }
}

class ClientHandler extends SimpleChannelHandler {
  override def channelConnected(ctx:ChannelHandlerContext, e:ChannelStateEvent) {
    println("Channel Connected")
  }

  override def channelDisconnected(ctx:ChannelHandlerContext, e:ChannelStateEvent) {
    println("Channel Disconnected")
  }

  override def exceptionCaught(ctx:ChannelHandlerContext, e:ExceptionEvent) {
    println("Exception Caught")
  }

  override def messageReceived(ctx:ChannelHandlerContext, e:MessageEvent) {
    println("Message Recieved " + e.getMessage)
  }
}