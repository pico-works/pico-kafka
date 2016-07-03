package org.pico.event.kafka.std

import org.apache.kafka.clients.NetworkClient
import org.pico.disposal.Disposable

package object networkClient {
  implicit val disposableNetworkClient_iZNAYP4 = new Disposable[NetworkClient] {
    override protected def onDispose(a: NetworkClient): Unit = a.close()
  }
}
