package org.pico.event.kafka.syntax

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import org.pico.event.{kafka => pico}

import scala.concurrent.duration._

package object kafkaConsumer {
//  implicit class KafkaConsumerOps_6p2svDZ[K, V](val self: KafkaConsumer[K, V]) extends AnyVal {
//    def forever(timeout: Duration = 1.second): Iterator[pico.ConsumerRecords[K, V]] = {
//      Iterator.continually(self.poll(timeout.toMillis))
//    }
//
//    def drain(timeout: Duration = 1.second): Iterator[pico.ConsumerRecords[K, V]] = {
//      forever(timeout).takeWhile(!_.isEmpty)
//    }
//
//    def source(timeout: Duration = 1.second): Source[pico.ConsumerRecords[K, V]] = {
//      val records: ConsumerRecords[K, V] = self.poll(1000)
//
//      ???
//    }
//  }
}
