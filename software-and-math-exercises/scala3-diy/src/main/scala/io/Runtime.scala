package io

import io.IO._

object Runtime {

  def unsafeRun[A](io: IO[A]): A = io match {
    case IO.Pure(a)        => a()
    case IO.FlatMap(io, f) => unsafeRun(f(unsafeRun(io)))
  }

}
