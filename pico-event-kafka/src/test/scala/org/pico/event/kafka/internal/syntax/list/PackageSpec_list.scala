package org.pico.event.kafka.internal.syntax.list

import org.specs2.mutable.Specification

class PackageSpec_list extends Specification {
  "List" should {
    "have filterOne operation" in {
      List(1, 2, 3, 4, 5, 6).filterOne(_ % 2 == 0) must_=== List(1, 3, 4, 5, 6)
      List.empty[Int].filterOne(_ % 2 == 0) must_=== List.empty
    }
  }
}
