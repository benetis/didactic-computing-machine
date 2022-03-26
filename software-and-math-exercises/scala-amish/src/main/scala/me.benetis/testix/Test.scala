package `me.benetis`
package testix

import me.benetis.io._

sealed trait TestResult
final case class TestSuccess() extends TestResult
final case class TestFailure() extends TestResult

final case class Test(name: String, run: () => IO[TestResult])

final case class TestSuite(tests: Test*)

trait TestDsl {
  def test(name: String)(run: => IO[TestResult]): Test = 
    Test(name, () => run)
  def suite(name: String)(tests: Test*): TestSuite = {
    TestSuite(tests: _*)
  }
}
