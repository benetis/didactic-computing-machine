package me.benetis
package io

import scala.util._
import me.benetis.io.IO._

object Runtime {
  def unsafeRun[A](io: IO[A]): Try[A] = interpret(io)

  private def interpret[A](io: IO[A]): Try[A] = io match {
    case Effect(a) => Try(a())
    case FlatMap(ctx, f: (Any => IO[A])) =>
      interpret(ctx) match {
        case Failure(exception) => Failure(exception)
        case Success(value)     => interpret(f(value))
      }
    case Pure(a) => Success(a())
    case Fail(e) => Failure(e)
    case Recover(ctx, f: (Throwable => IO[A])) => interpret(ctx) match {
      case Failure(exception) => interpret(f(exception))
      case s @ Success(value) => s
    }
  }
}
