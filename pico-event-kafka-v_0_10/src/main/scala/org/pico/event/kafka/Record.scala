package org.pico.event.kafka

import org.apache.kafka.common.record.TimestampType

case class Record[K, V](
    topic: String,
    partition: Int,
    offset: Long,
    timestamp: Long,
    timestampType: TimestampType,
    checksum: Long,
    serializedKeySize: Int,
    serializedValueSize: Int,
    key: K,
    value: V)
