package me.benetis
package io

sealed trait IO[A] { self =>
  def flatMap[B](f: A => IO[B]): IO[B] = IO.FlatMap(self, f)
  def map[B](f: A => B): IO[B] = flatMap(a => IO.pure(f(a)))
  def *>[B](right: IO[B]): IO[B] = flatMap(_ => right)
}

object IO {
  case class Pure[A](a: () => A) extends IO[A]
  case class FlatMap[A, B](ctx: IO[A], f: A => IO[B]) extends IO[B]
  case class Effect[A](a: () => A) extends IO[A] 
  def effect[A](a: => A): IO[A] = Effect(() => a)
  def pure[A](a: A): IO[A] = Pure(() => a)
}
