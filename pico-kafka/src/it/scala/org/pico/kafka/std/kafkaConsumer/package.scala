package org.pico.kafka.std

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.pico.disposal.Disposable

package object kafkaConsumer {
  implicit def disposableKafkaConsumer_VAmM4XK[K, V] = new Disposable[KafkaConsumer[K, V]] {
    override protected def onDispose(a: KafkaConsumer[K, V]): Unit = a.close()
  }
}
