package com.openaf.rmi.common

import org.jboss.netty.channel.ChannelPipeline
import org.jboss.netty.handler.codec.serialization.{ClassResolvers, ObjectDecoder, ObjectEncoder}

object RMICommon {
  def addHandlers(pipeline:ChannelPipeline, classLoader:ClassLoader) {
    pipeline.addLast("objectEncoder", new ObjectEncoder)
    pipeline.addLast("objectDecoder", new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(classLoader)))
  }
}
