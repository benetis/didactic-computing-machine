package me.benetis
package example

import io._

object Main extends App {

  val program = (for {
    _ <- IO.effect(println("Hello"))
    _ <- IO.effect(throw new Throwable("fuck this"))
    _ <- IO.effect(println("IO"))
  } yield ()).recover(t => IO.effect(println(t.getMessage())))

  Runtime.unsafeRun(program)
}
