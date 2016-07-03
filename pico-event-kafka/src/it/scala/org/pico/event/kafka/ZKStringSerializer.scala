package org.pico.event.kafka

import org.I0Itec.zkclient.serialize.ZkSerializer

object ZKStringSerializer extends ZkSerializer {
  def serialize(data : Object) : Array[Byte] = data.asInstanceOf[String].getBytes("UTF-8")

  def deserialize(bytes : Array[Byte]) : Object = {
    if (bytes == null) {
      null
    } else {
      new String(bytes, "UTF-8")
    }
  }
}
