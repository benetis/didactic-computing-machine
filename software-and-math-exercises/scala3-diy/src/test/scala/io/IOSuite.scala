package io

import io.IO._

import scala.util.*

class IOSuite extends munit.FunSuite {
  test("IO.pure(42) should be lazy") {
    var evaluated = false
    val io = IO.pure {
      evaluated = true
      42
    }
    assert(!evaluated)
    assertEquals(Runtime.unsafeRun(io), Success(42))
    assert(evaluated)
  }

  test("IO.effect with thrown exception should result in Failure") {
    val io = IO.effect(throw new Exception("Boom!"))

    runAndExpectFailure(io, "Boom!")
  }

  test("IO.effect should be lazy") {
    var evaluated = false
    val io = IO.effect {
      evaluated = true
      42
    }
    assert(!evaluated)
    assertEquals(Runtime.unsafeRun(io), Success(42))
    assert(evaluated)
  }

  test("IO.fail should result in Failure") {
    val io = IO.fail(new Exception("Boom!"))

    runAndExpectFailure(io, "Boom!")
  }

  def runAndExpectFailure[A](io: IO[A], expectedMessage: String): Unit =
    Runtime.unsafeRun(io) match
      case Failure(exception) =>
        assertEquals(exception.getMessage, expectedMessage)
      case Success(_) => assert(false, "Expected Failure, got Success")

}
