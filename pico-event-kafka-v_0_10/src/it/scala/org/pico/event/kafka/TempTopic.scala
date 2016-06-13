package org.pico.event.kafka

import java.io.Closeable

trait TempTopic extends Closeable {
  def name: String
}
