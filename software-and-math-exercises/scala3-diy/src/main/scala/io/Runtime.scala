package io

import io.IO.*

import scala.util._

object Runtime {

  def unsafeRun[A](io: IO[A]): Try[A] = io match {
    case IO.Pure(a)   => Success(a())
    case IO.Effect(f) => Try(f())
    case IO.FlatMap(io, f) =>
      unsafeRun(io) match
        case Failure(exception) => Failure(exception)
        case Success(value)     => unsafeRun(f(value))
  }

}
