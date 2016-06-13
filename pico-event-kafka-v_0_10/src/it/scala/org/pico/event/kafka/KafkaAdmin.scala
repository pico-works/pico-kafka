package org.pico.event.kafka

import java.io.Closeable
import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import kafka.admin.{AdminUtils, RackAwareMode}
import kafka.common.TopicExistsException
import kafka.utils.ZkUtils
import org.I0Itec.zkclient.{ZkClient, ZkConnection}
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.{Disposer, OnClose}

class KafkaAdmin(zookeeperConnect: String) extends Disposer {
  val sessionTimeoutMs = 10 * 1000
  val connectionTimeoutMs = 8 * 1000
  val zkClient = this.disposesOrClose(new ZkClient(zookeeperConnect, sessionTimeoutMs, connectionTimeoutMs, ZKStringSerializer) with Closeable)
  val zkUtils = this.disposesOrClose(new ZkUtils(zkClient, new ZkConnection(zookeeperConnect), isSecure = false) with Closeable)

  def createTopic(
      topic: String,
      partitions: Int,
      replicationFactor: Int,
      topicConfig: Properties = new Properties,
      rackAwareMode: RackAwareMode = RackAwareMode.Enforced): Unit = {
  }

  def forceCreateTopic(
      topic: String,
      partitions: Int,
      replicationFactor: Int,
      topicConfig: Properties = new Properties,
      rackAwareMode: RackAwareMode = RackAwareMode.Enforced): Unit = {
    try {
      createTopic(topic, partitions, replicationFactor, topicConfig, rackAwareMode)
    } catch {
      case e: TopicExistsException =>
        deleteTopic(topic)
        createTopic(topic, partitions, replicationFactor, topicConfig, rackAwareMode)
    }
  }

  def deleteTopic(topic: String): Unit = AdminUtils.deleteTopic(zkUtils, topic: String)

  def createTempName(): String = {
    val formatter = new SimpleDateFormat("yyyy-MM-dd")
    val dateText = formatter.format(new Date())
    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val suffix = Iterator.fill(7)(scala.util.Random.nextInt(alphabet.length)).map(alphabet).mkString
    s"tmp-$dateText-$suffix"
  }

  def tempTopic(
      partitions: Int,
      replicationFactor: Int,
      topicConfig: Properties = new Properties,
      rackAwareMode: RackAwareMode = RackAwareMode.Enforced): Closeable = {
    val topicName = createTempName()
    createTopic(topicName, partitions, replicationFactor, topicConfig, rackAwareMode)
    OnClose(deleteTopic(topicName))
  }

  def forceTempTopic(
      partitions: Int,
      replicationFactor: Int,
      topicConfig: Properties = new Properties,
      rackAwareMode: RackAwareMode = RackAwareMode.Enforced): TempTopic = {
    val topicName = createTempName()
    forceCreateTopic(topicName, partitions, replicationFactor, topicConfig, rackAwareMode)
    new TempTopic {
      override def name: String = topicName
      override def close(): Unit = deleteTopic(name)
    }
  }
}
