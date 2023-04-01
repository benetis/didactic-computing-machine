package io

class IOSuite extends munit.FunSuite {
  test("Evaluating IO.pure(42) should be lazy") {
    var evaluated = false
    val io = IO.pure {
      evaluated = true
      42
    }
    assert(!evaluated)
    assertEquals(Runtime.unsafeRun(io), 42)
    assert(evaluated)
  }

}
