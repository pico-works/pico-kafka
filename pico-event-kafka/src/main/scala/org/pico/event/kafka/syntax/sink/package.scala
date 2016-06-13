package org.pico.event.kafka.syntax

import org.pico.event.Sink
import org.pico.event.kafka.EncodeKafka
import org.pico.event.kafka.syntax.encodeKafka._

package object sink {
  implicit class SinkOps_xxx(val self: Sink[(Array[Byte], Array[Byte])]) extends AnyVal {
    def encodeKafka[K, V]()(implicit evK: EncodeKafka[K], evV: EncodeKafka[V]): Sink[(K, V)] = {
      self.comap[(K, V)] { case (ka, va) =>
        ka.encodeKafka -> va.encodeKafka
      }
    }
  }
}
