package io

import io.IO

enum IO[+A]:
  case Pure(a: () => A) extends IO[A]
  case FlatMap[B, A](io: IO[B], f: B => IO[A]) extends IO[A]
  case Effect(io: () => A) extends IO[A]
  case Fail(e: Throwable) extends IO[Nothing]
  case RecoverWith(io: IO[A], f: Throwable => IO[A]) extends IO[A]

  def flatMap[B](f: A => IO[B]): IO[B] = IO.FlatMap(this, f)
  def map[B](f: A => B): IO[B] = flatMap(a => IO.Pure(() => f(a)))
  def recoverWith[B >: A](f: Throwable => IO[B]): IO[B] =
    RecoverWith(this, f)
  def *>[B](io: IO[B]): IO[B] = flatMap(_ => io)

object IO {
  def pure[A](a: => A): IO[A] = IO.Pure(() => a)
  def effect[A](io: => A): IO[A] = IO.Effect(() => io)
  def fail[A](e: Throwable): IO[A] = IO.Fail(e)
}
