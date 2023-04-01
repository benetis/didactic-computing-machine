package io

import scala.util._

class IOSuite extends munit.FunSuite {
  test("Evaluating IO.pure(42) should be lazy") {
    var evaluated = false
    val io = IO.pure {
      evaluated = true
      42
    }
    assert(!evaluated)
    assertEquals(Runtime.unsafeRun(io), Success(42))
    assert(evaluated)
  }

  test("Evaluating IO.effect with thrown exception should result in Failure") {
    val io = IO.effect(throw new Exception("Boom!"))

    Runtime.unsafeRun(io) match
      case Failure(exception) => assertEquals(exception.getMessage, "Boom!")
      case Success(_)         => assert(false, "Expected Failure, got Success")
  }

  test("Evaluating IO.effect should be lazy") {
    var evaluated = false
    val io = IO.effect {
      evaluated = true
      42
    }
    assert(!evaluated)
    assertEquals(Runtime.unsafeRun(io), Success(42))
    assert(evaluated)
  }

}
