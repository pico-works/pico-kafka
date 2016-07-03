package org.pico.kafka

import java.io.Closeable

trait TempTopic extends Closeable {
  def name: String
}
