package com.openaf.rmi.common

import org.jboss.netty.channel.ChannelPipeline
import org.jboss.netty.handler.codec.serialization._
import java.io.{ByteArrayOutputStream, ByteArrayInputStream}

object RMICommon {
  def addHandlers(pipeline:ChannelPipeline, classLoader:ClassLoader) {
    pipeline.addLast("objectEncoder", new ObjectEncoder)
    pipeline.addLast("objectDecoder", new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(classLoader)))
  }
}

case class MethodInvocationRequest(id:Long, className:String, methodName:String, params:Array[String], paramValuesBytes:Array[Array[Byte]])
case class MethodInvocationResult(id:Long, result:Array[Byte])

trait ServicesListing {
  def services:List[String]
}

object DefaultObjectEncoder {
  def encode(obj:AnyRef) = {
    val byteArrayOutputStream = new ByteArrayOutputStream
    val objectEncoderOutputStream = new ObjectEncoderOutputStream(byteArrayOutputStream)
    objectEncoderOutputStream.writeObject(obj)
    objectEncoderOutputStream.flush()
    objectEncoderOutputStream.close()
    byteArrayOutputStream.toByteArray
  }
  def decode(bytes:Array[Byte], classLoader:ClassLoader) = {
    val byteArrayInputStream = new ByteArrayInputStream(bytes)
    val objectDecoderInputStream = new ObjectDecoderInputStream(byteArrayInputStream, classLoader)
    objectDecoderInputStream.readObject
  }
}