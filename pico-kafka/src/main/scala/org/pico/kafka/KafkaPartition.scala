package org.pico.kafka

trait KafkaPartition[A] {
  def kafkaPartition(a: A): Int
}
