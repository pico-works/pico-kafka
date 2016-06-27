package org.pico.event.kafka

import java.util
import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.PartitionInfo
import org.pico.event.Sink
import org.pico.event.kafka.syntax.encodeKafka._
import org.pico.event.kafka.syntax.kafkaPartition._

class KafkaEventProducer(properties: Properties) {
  private val producer = new KafkaProducer[Array[Byte], Array[Byte]](properties)

  def sink[K: EncodeKafka: KafkaPartition, V: EncodeKafka](topic: String): Sink[(K, V)] = {
    val partitions: util.List[PartitionInfo] = producer.partitionsFor(topic)
    println(s"==> partitions for $topic = $partitions")

    Sink { case (k, v) =>
      producer.send(new ProducerRecord(topic, k.kafkaPartition % partitions.size, k.encodeKafka, v.encodeKafka))
    }
  }

  def sink[K: EncodeKafka: KafkaPartition, V: EncodeKafka, C](topic: String, completedSink: Sink[C]): Sink[(K, V, C)] = {
    val partitions = producer.partitionsFor(topic)
    println(s"==> partitions for $topic = $partitions")

    Sink { case (k, v, c) =>
      producer.send(new ProducerRecord(topic, k.kafkaPartition % partitions.size, k.encodeKafka, v.encodeKafka), new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          completedSink.publish(c)
        }
      })
    }
  }
}
