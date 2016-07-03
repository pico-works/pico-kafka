package org.pico.kafka.std.all

import org.apache.kafka.clients.consumer.{Consumer, ConsumerInterceptor, KafkaConsumer, MockConsumer}
import org.apache.kafka.clients.producer._
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification

class PackageSpec extends Specification {
  "Should be able to call dispose method on any Kafka type with close method" in {
    if (false) {
      val a: Consumer[Int, String] = ???
      val b: KafkaConsumer[Int, String] = ???
      val c: MockConsumer[Int, String] = ???
      val d: ConsumerInterceptor[Int, String] = ???
      val e: Producer[Int, String] = ???
      val f: KafkaProducer[Int, String] = ???
      val g: MockProducer[Int, String] = ???
      val h: ProducerInterceptor[Int, String] = ???

      a.dispose()
      b.dispose()
      c.dispose()
      d.dispose()
      e.dispose()
      f.dispose()
      g.dispose()
      h.dispose()
    }

    success
  }
}
