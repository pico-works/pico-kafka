package org.pico.event.kafka

import java.io.Closeable
import java.util.Properties

import org.pico.disposal.Disposer
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification
import org.pico.event.kafka.syntax.decodeKafka._
import org.pico.event.kafka.syntax.encodeKafka._

import scala.concurrent.duration._

class RebalanceItSpec extends Specification {
  implicit val stringKafkaPartition = new KafkaPartition[String] {
    override def kafkaPartition(a: String): Int = a.hashCode
  }

  implicit val encodeKafka = new EncodeKafka[String] {
    override def encodeKafka(a: String): Array[Byte] = a.getBytes()
  }

  implicit val decodeKafka = new DecodeKafka[String] {
    override def decodeKafka(buffer: Array[Byte]): Option[String] = Some(new String(buffer, "UTF-8"))
  }

  "Rebalance events" should {
    "work as expected" in {
      for (disposer <- Disposer();
           kafkaAdmin <- new KafkaAdmin("192.168.99.100:2181");
           tempTopic <- kafkaAdmin.forceTempTopic(partitions = 10, replicationFactor = 1)) {

        val properties = new Properties()
        properties.put("bootstrap.servers", "192.168.99.100:9092")
        properties.put("group.id", s"${tempTopic.name}-group")
        properties.put("auto.offset.reset", "earliest")
        properties.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
        properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")

        val kafkaEventProducer = new KafkaEventProducer(properties)
        val sink = kafkaEventProducer.sink[String, String](tempTopic.name)

        val consumers = (0 until 2).map(id => disposer.disposes(new KafkaEventConsumer(properties))).toList

        val rebalanceViews = consumers.zipWithIndex.map { case (consumer, id) =>
          disposer += consumer.rebalanceSource.foldRight(List.empty[Rebalance])(_ :: _)
        }

        val eventViews: List[Closeable] = consumers.zipWithIndex.map { case (consumer, id) =>
          disposer += consumer.sourceForTopics(tempTopic.name).subscribe { r =>
            println(s"==> event[$id]: ${new String(r.value)}")
          }
        }

        (0 until 2).foreach { index =>
          println(s"==> Creating consumer $index")
          val kafkaEventConsumer = new KafkaEventConsumer(properties)

          disposer += kafkaEventConsumer.sourceForTopics(tempTopic.name).subscribe { r =>
            println(s"==> event[$index]: ${new String(r.value)}")
          }

          disposer += kafkaEventConsumer.rebalanceSource.subscribe {
            case PartitionsAssigned(partitions) =>
              println(s"====> thread $index assigned: ${partitions.toList}")
            case PartitionsRevoked(partitions) =>
              println(s"====> thread $index revoked: ${partitions.toList}")
          }

          disposer += kafkaEventConsumer.run(1.second)
        }

        (0 until 20).foreach { index =>
          sink.publish(s"key$index" -> s"value$index")
          Thread.sleep(1000)
        }

        consumers.foreach { consumer =>
          println("==========")
          consumer.metrics.foreach { case (k, v) =>
            println(s"==> $k, $v")
          }
        }


        success
      }
    }
  }
}
