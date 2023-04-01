package me.benetis
package io

sealed trait IO[+A] { self =>
  def flatMap[B](f: A => IO[B]): IO[B] = IO.FlatMap(self, f)
  def map[B](f: A => B): IO[B] = flatMap(a => IO.pure(f(a)))
  def *>[B](right: IO[B]): IO[B] = flatMap(_ => right)
  def recover[B >: A](f: Throwable => IO[B]): IO[B] = IO.Recover(self, f)
}

object IO {
  case class Pure[A](a: () => A) extends IO[A]
  case class FlatMap[A, B](ctx: IO[A], f: A => IO[B]) extends IO[B]
  case class Fail[A](e: Throwable) extends IO[A]
  case class Recover[A](ctx: IO[A], f: Throwable => IO[A]) extends IO[A]
  case class Effect[A](a: () => A) extends IO[A]

  def effect[A](a: => A): IO[A] = Effect(() => a)
  def pure[A](a: A): IO[A] = Pure(() => a)
}
