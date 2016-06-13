package org.pico.event.kafka.syntax

import org.pico.event.kafka.EncodeKafka

package object encodeKafka {
  implicit class EncodeKafkaOps_Kz2EJso[A](val self: A) extends AnyVal {
    def encodeKafka(implicit ev: EncodeKafka[A]): Array[Byte] = ev.encodeKafka(self)
  }
}
