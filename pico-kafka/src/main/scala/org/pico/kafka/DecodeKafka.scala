package org.pico.kafka

trait DecodeKafka[A] {
  def decodeKafka(buffer: Array[Byte]): Option[A]
}

object DecodeKafka {
  def apply[A](f: Array[Byte] => Option[A]): DecodeKafka[A] = new DecodeKafka[A] {
    override def decodeKafka(buffer: Array[Byte]): Option[A] = f(buffer)
  }

  implicit val decodeKafka_ArrayOfByte_BziJvZ2 = new DecodeKafka[Array[Byte]] {
    override def decodeKafka(buffer: Array[Byte]): Option[Array[Byte]] = Some(buffer)
  }
}
