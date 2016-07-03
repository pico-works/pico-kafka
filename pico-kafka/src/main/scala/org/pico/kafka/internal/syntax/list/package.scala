package org.pico.kafka.internal.syntax

import scala.annotation.tailrec

package object list {
  implicit class ListOps_6NeNtuY[A](val self: List[A]) extends AnyVal {
    def pushAll(that: List[A]): List[A] = {
      @tailrec
      def go(ls: List[A], rs: List[A]): List[A] = {
        ls match {
          case x :: xs  => go(xs, x :: rs)
          case Nil      => rs
        }
      }

      go(that, self)
    }

    def popUntil(f: A => Boolean): (List[A], List[A]) = {
      @tailrec
      def go(ls: List[A], rs: List[A]): (List[A], List[A]) = {
        rs match {
          case x :: xs  => if (f(x)) (ls, rs) else go(x :: ls, xs)
          case Nil      => (ls, rs)
        }
      }

      go(List.empty, self)
    }

    def filterOne(f: A => Boolean): List[A] = {
      val (ls, rs) = popUntil(f)
      rs.drop(1).pushAll(ls)
    }
  }
}
