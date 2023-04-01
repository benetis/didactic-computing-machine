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

}
