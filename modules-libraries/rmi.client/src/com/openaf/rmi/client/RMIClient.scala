package com.openaf.rmi.client

import org.apache.mina.transport.socket.nio.NioSocketConnector
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory
import org.apache.mina.filter.codec.ProtocolCodecFilter

class RMIClient(hostName:String, port:Int) {
  def connect() {
    val connector = new NioSocketConnector
    connector.getFilterChain.addLast("Serialization", new ProtocolCodecFilter(new ObjectSerializationCodecFactory))
  }
}
