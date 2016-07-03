package org.pico.kafka.syntax

import org.pico.kafka.DecodeKafka

package object decodeKafka {
  implicit class DecodeKafkaOps_Kz2EJso(val self: Array[Byte]) extends AnyVal {
    def decodeKafka[A](implicit ev: DecodeKafka[A]): Option[A] = ev.decodeKafka(self)
  }
}
