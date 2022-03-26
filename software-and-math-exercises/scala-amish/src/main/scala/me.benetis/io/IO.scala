package me.benetis
package io

sealed trait IO[A] { self =>
  def run: A
  def map[B](f: A => B): IO[B] =
    new IO[B] {
      def run = f(self.run)
    }
  def flatMap[B](f: A => IO[B]): IO[B] =
    new IO[B] {
      def run: B = f(self.run).run
    }
  def *>[B](right: IO[B]): IO[B] = flatMap(_ => right)
}

object IO {
  case class Effect[A](a: () => A) extends IO[A] {
    def run: A = a()
  }
  def effect[A](a: => A): IO[A] = Effect(() => a)
}
