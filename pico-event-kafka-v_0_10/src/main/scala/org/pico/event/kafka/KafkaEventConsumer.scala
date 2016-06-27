package org.pico.event.kafka

import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean
import java.util.{Properties, Collection => JCollection}

import org.apache.kafka.clients.consumer.{ConsumerRebalanceListener, ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.TopicPartition
import org.pico.disposal.{Disposer, SimpleDisposer}
import org.pico.disposal.std.autoCloseable._
import org.pico.event.kafka.internal.syntax.list._
import org.pico.event.{Bus, Cell, Source, View}

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration

class KafkaEventConsumer(properties: Properties) extends SimpleDisposer {
  private val consumer = new KafkaConsumer[Array[Byte], Array[Byte]](properties)
  private val rebalanceBus = Bus[Rebalance]
  private val consumerRecordsBus = Bus[Record[Array[Byte], Array[Byte]]]
  private val topicsRef = Cell(List.empty[String])

  private val rebalanceListener = new ConsumerRebalanceListener {
    override def onPartitionsAssigned(partitions: JCollection[TopicPartition]): Unit = {
      rebalanceBus.publish(PartitionsAssigned(partitions.asScala.toSeq))
    }

    override def onPartitionsRevoked(partitions: JCollection[TopicPartition]): Unit = {
      rebalanceBus.publish(PartitionsRevoked(partitions.asScala.toSeq))
    }
  }

  def rebalanceSource: Source[Rebalance] = rebalanceBus
  def consumerRecordsSource: Source[Record[Array[Byte], Array[Byte]]] = consumerRecordsBus
  def sourceForTopics: View[List[String]] = topicsRef

  this.disposes(topicsRef.source.subscribe { topics =>
    consumer.subscribe(topicsRef.value.asJava, rebalanceListener)
  })

  def sourceForTopics(topics: String*): Source[Record[Array[Byte], Array[Byte]]] = {
    val source = consumerRecordsBus.filter(r => topics.contains(r.topic))
    topicsRef.update(ts => (ts /: topics) { case (us, u) => u :: us })
    source.onClose(topicsRef.update(ts => (ts /: topics) { case (existingTopics, topic) =>
      existingTopics.filterOne(_ == topic)
    }))
    source
  }

  def run(timeout: Duration): Closeable = {
    val thread = new Thread with SimpleDisposer {
      val running = this.resets(false, new AtomicBoolean(true))

      override def run(): Unit = {
        while (running.get()) {
          consumer.poll(timeout.toMillis).asScala.foreach { kafkaRecord =>
            val record = Record(
                topic               = kafkaRecord.topic,
                partition           = kafkaRecord.partition,
                offset              = kafkaRecord.offset,
                timestamp           = kafkaRecord.timestamp,
                timestampType       = kafkaRecord.timestampType,
                checksum            = kafkaRecord.checksum,
                serializedKeySize   = kafkaRecord.serializedKeySize,
                serializedValueSize = kafkaRecord.serializedValueSize,
                key                 = kafkaRecord.key,
                value               = kafkaRecord.value)

            consumerRecordsBus.publish(record)
          }
        }
      }

      this.onClose(this.interrupt())
    }

    thread.setDaemon(true)
    thread.start()

    thread
  }
}
