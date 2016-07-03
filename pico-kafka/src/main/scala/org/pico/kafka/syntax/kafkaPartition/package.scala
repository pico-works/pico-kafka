package org.pico.kafka.syntax

import org.pico.kafka.KafkaPartition

package object kafkaPartition {
  implicit class KafkaPartitionOps_sWVM7CU[A](val self: A) extends AnyVal {
    def kafkaPartition(implicit ev: KafkaPartition[A]): Int = ev.kafkaPartition(self)
  }
}
