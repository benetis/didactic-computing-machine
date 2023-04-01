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

  test("IO.recoverWith should recover from failure") {
    val io = IO.fail(new Exception("Boom!")).recoverWith(_ => IO.pure(42))

    assertEquals(Runtime.unsafeRun(io), Success(42))
  }

  test("IO *> should run effects in sequence") {
    var first = false
    var second = false
    val io = IO.effect { first = true } *> IO.effect { second = true }

    assertEquals(Runtime.unsafeRun(io), Success(()))
    assert(first)
    assert(second)
  }

  test("IO.acquireAndRelease should acquire and release resources") {
    var acquire = false
    var release = false

    var inUse = 0

    val io = IO.acquireAndRelease(
      IO.effect { acquire = true; 42 },
      _ => IO.effect { release = true },
      a => IO.effect { inUse = a }
    )

    assert(!acquire)
    assert(!release)
    assertEquals(Runtime.unsafeRun(io), Success(()))
    assert(acquire)
    assert(release)
    assertEquals(inUse, 42)
  }

  def runAndExpectFailure[A](io: IO[A], expectedMessage: String): Unit =
    Runtime.unsafeRun(io) match
      case Failure(exception) =>
        assertEquals(exception.getMessage, expectedMessage)
      case Success(_) => assert(false, "Expected Failure, got Success")

}
