package org.pico.event.kafka

import java.util.Properties

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification

import scala.concurrent.duration._

class KafkaSourceItSpec extends Specification {
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
      val sink = kafkaEventProducer.sink(tempTopic.name)

      sink.publish("key".getBytes -> "value1".getBytes)

      val kafkaEventConsumer = new KafkaEventConsumer(properties)

      val subscription = kafkaEventConsumer.topics(tempTopic.name).subscribe { r =>
        println(s"event: ${new String(r.value)}")
      }

      kafkaEventConsumer.run(1.second)
      sink.publish("key".getBytes -> "value2".getBytes)

      Thread.sleep(1000)

      success
    }
  }
}
