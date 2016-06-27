package org.pico.event.kafka.syntax

import org.pico.event.kafka.KafkaPartition

package object kafkaPartition {
  implicit class KafkaPartitionOps_sWVM7CU[A](val self: A) extends AnyVal {
    def kafkaPartition(implicit ev: KafkaPartition[A]): Int = ev.kafkaPartition(self)
  }
}
