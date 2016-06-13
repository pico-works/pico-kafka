package org.pico.event.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.pico.event.Sink
import org.pico.event.kafka.syntax.encodeKafka._

class KafkaEventProducer(properties: Properties) {
  private val producer = new KafkaProducer[Array[Byte], Array[Byte]](properties)

  def sink[K: EncodeKafka, V: EncodeKafka](topic: String): Sink[(K, V)] = {
    Sink { case (k, v) =>
      producer.send(new ProducerRecord(topic, k.encodeKafka, v.encodeKafka))
    }
  }

  def sink[K: EncodeKafka, V: EncodeKafka, C](topic: String, completedSink: Sink[C]): Sink[(K, V, C)] = {
    Sink { case (k, v, c) =>
      producer.send(new ProducerRecord(topic, k.encodeKafka, v.encodeKafka), new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          completedSink.publish(c)
        }
      })
    }
  }
}
