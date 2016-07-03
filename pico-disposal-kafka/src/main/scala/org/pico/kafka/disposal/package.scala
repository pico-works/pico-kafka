package org.pico.kafka

import org.apache.kafka.clients.{KafkaClient, NetworkClient}
import org.apache.kafka.clients.consumer.{Consumer, ConsumerInterceptor, KafkaConsumer, MockConsumer}
import org.apache.kafka.clients.producer._
import org.pico.disposal.Disposable

package object disposal {
  implicit def disposal_Consumer_7NdDM8Y[K, V] = Disposable[Consumer[K, V]](_.close())

  implicit def disposal_KafkaConsumer_7NdDM8Y[K, V] = Disposable[KafkaConsumer[K, V]](_.close())

  implicit def disposal_MockConsumer_7NdDM8Y[K, V] = Disposable[MockConsumer[K, V]](_.close())

  implicit def disposal_ConsumerInterceptor_7NdDM8Y[K, V] = Disposable[ConsumerInterceptor[K, V]](_.close())

  implicit def disposal_Producer_7NdDM8Y[K, V] = Disposable[Producer[K, V]](_.close())

  implicit def disposal_KafkaProducer_7NdDM8Y[K, V] = Disposable[KafkaProducer[K, V]](_.close())

  implicit def disposal_MockProducer_7NdDM8Y[K, V] = Disposable[MockProducer[K, V]](_.close())

  implicit def disposal_ProducerInterceptor_7NdDM8Y[K, V] = Disposable[ProducerInterceptor[K, V]](_.close())

  implicit val disposal_Partitioner_7NdDM8Y = Disposable[Partitioner](_.close())

  implicit val disposal_NetworkClient_7NdDM8Y = Disposable[NetworkClient](_.close())

  implicit val disposal_KafkaClient_7NdDM8Y = Disposable[KafkaClient](_.close())
}
