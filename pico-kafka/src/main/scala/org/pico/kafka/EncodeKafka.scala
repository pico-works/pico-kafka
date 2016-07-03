package org.pico.kafka

trait EncodeKafka[A] {
  def encodeKafka(a: A): Array[Byte]
}

object EncodeKafka {
  def apply[A](f: A => Array[Byte]) = new EncodeKafka[A] {
    override def encodeKafka(a: A): Array[Byte] = f(a)
  }

  implicit val encodeKafka_ArrayOfByte_BqDWwf9 = new EncodeKafka[Array[Byte]] {
    override def encodeKafka(a: Array[Byte]): Array[Byte] = a
  }
}
