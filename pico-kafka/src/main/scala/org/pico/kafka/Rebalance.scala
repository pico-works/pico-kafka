package org.pico.kafka

import org.apache.kafka.common.TopicPartition

sealed trait Rebalance

case class PartitionsAssigned(partitions: Seq[TopicPartition]) extends Rebalance
case class PartitionsRevoked(partitions: Seq[TopicPartition]) extends Rebalance
