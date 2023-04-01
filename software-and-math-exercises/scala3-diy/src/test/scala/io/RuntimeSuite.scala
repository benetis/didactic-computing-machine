package io

import scala.util._

class RuntimeSuite extends munit.FunSuite {
  test("Evaluating IO.pure(42) should return 42") {
    assertEquals(Runtime.unsafeRun(IO.pure(42)), Success(42))
  }
}
