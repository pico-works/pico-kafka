package org.pico.event.kafka

trait KafkaPartition[A] {
  def kafkaPartition(a: A): Int
}
