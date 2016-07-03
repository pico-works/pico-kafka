package org.pico.event.kafka.std

import org.apache.kafka.clients.producer.KafkaProducer
import org.pico.disposal.Disposable

package object kafkaProducer {
  implicit def disposableKafkaProducer_hBxdxpz[K, V] = new Disposable[KafkaProducer[K, V]] {
    override protected def onDispose(a: KafkaProducer[K, V]): Unit = a.close()
  }
}
