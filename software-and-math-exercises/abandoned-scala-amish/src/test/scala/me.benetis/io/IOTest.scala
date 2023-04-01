package `me.benetis`
package io

import testix._
import me.benetis.io._

object IOTest extends RunnableTests {
  def suites = suite("")(
    test("Test")(
      assertEqualM(IO.pure(1), IO.pure(1))
    ),  
    test("Test2")(
      assert("a" == "a")
    )
  )
}
