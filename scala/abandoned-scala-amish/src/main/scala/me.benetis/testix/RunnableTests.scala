package `me.benetis`
package testix

trait RunnableTests extends TestDsl with AssertDsl {
  def suites: TestSuite
}
