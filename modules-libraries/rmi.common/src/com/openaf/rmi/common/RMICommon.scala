package com.openaf.rmi.common

import org.jboss.netty.channel.ChannelPipeline
import org.jboss.netty.handler.codec.serialization._
import java.io.{ObjectOutputStream, ByteArrayOutputStream, ByteArrayInputStream}

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
    // TODO - Either get netty to add a OSGI aware ObjectEncoderOutputStream or write one myself
//    val objectOutputStream = new ObjectEncoderOutputStream(byteArrayOutputStream)
    val objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(obj)
    objectOutputStream.flush()
    objectOutputStream.close()
    byteArrayOutputStream.toByteArray
  }
  def decode(bytes:Array[Byte], classLoader:ClassLoader) = {
    val byteArrayInputStream = new ByteArrayInputStream(bytes)
    // TODO - Either get netty to add a OSGI aware ObjectDecoderInputStream or write one myself
    // TODO - Either get netty to add a OSGI aware ObjectDecoderInputStream or write one myself
//    val objectInputStream = new ObjectDecoderInputStream(byteArrayInputStream, classLoader)
    val objectInputStream = new OSGIAwareObjectInputStream(byteArrayInputStream, classLoader)
    objectInputStream.readObject
  }
}