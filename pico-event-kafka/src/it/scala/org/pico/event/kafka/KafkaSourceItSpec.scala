package org.pico.event.kafka

import java.util.Properties

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification

import scala.concurrent.duration._

class KafkaSourceItSpec extends Specification {
  implicit val stringKafkaPartition = new KafkaPartition[String] {
    override def kafkaPartition(a: String): Int = a.hashCode
  }

  implicit val encodeKafka = new EncodeKafka[String] {
    override def encodeKafka(a: String): Array[Byte] = a.getBytes()
  }

  implicit val decodeKafka = new DecodeKafka[String] {
    override def decodeKafka(buffer: Array[Byte]): Option[String] = Some(new String(buffer, "UTF-8"))
  }

  "Kafka Admin" should {
    for ( kafkaAdmin  <- new KafkaAdmin("192.168.99.100:2181");
          tempTopic   <- kafkaAdmin.forceTempTopic(partitions = 1, replicationFactor = 1)) {
      val properties = new Properties()
      properties.put("bootstrap.servers"  , "192.168.99.100:9092")
      properties.put("group.id"           , s"${tempTopic.name}-group")
      properties.put("auto.offset.reset"  , "earliest")
      properties.put("key.serializer"     , "org.apache.kafka.common.serialization.ByteArraySerializer")
      properties.put("value.serializer"   , "org.apache.kafka.common.serialization.ByteArraySerializer")
      properties.put("key.deserializer"   , "org.apache.kafka.common.serialization.ByteArrayDeserializer")
      properties.put("value.deserializer" , "org.apache.kafka.common.serialization.ByteArrayDeserializer")

      val kafkaEventProducer = new KafkaEventProducer(properties)
      val sink = kafkaEventProducer.sink[String, String](tempTopic.name)

      sink.publish("key" -> "value1")

      val kafkaEventConsumer = new KafkaEventConsumer(properties)

      val subscription = kafkaEventConsumer.sourceForTopics(tempTopic.name).subscribe { r =>
        println(s"event: ${new String(r.value)}")
      }

      kafkaEventConsumer.run(1.second)
      sink.publish("key" -> "value2")

      Thread.sleep(1000)

      success
    }
  }
}
