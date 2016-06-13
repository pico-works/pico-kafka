package org.pico.event.kafka.syntax

import org.pico.event.Source
import org.pico.event.kafka.DecodeKafka
import org.pico.event.kafka.syntax.decodeKafka._

package object source {
  implicit class SourceOps_xxx(val self: Source[(Array[Byte], Array[Byte])]) extends AnyVal {
    def decodeKafka[K, V]()(implicit evK: DecodeKafka[K], evV: DecodeKafka[V]): Source[(K, V)] = {
      self.mapConcat { case (ka, va) =>
        ka.decodeKafka[K] match {
          case Some(k) => va.decodeKafka[V] match {
            case Some(v) => Some(k -> v)
            case None => None
          }
          case None => None
        }
      }
    }
  }
}
