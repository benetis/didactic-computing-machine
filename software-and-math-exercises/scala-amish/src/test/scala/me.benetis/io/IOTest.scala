package `me.benetis`
package io

import testix._
import me.benetis.io._

object IOTest extends RunnableTests {
  suite("")(
    test("Test")(
      IO.pure(TestSuccess())
    ),  
    test("Test2")(
      IO.pure(TestFailure())
    )
  )
}
