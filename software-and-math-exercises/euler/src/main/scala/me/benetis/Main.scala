package me.benetis

object Main extends App {
  val problems: Vector[() => String] = Vector(
    Problems.problem1,
    Problems.problem2
  )

  println(problems.last())
}
