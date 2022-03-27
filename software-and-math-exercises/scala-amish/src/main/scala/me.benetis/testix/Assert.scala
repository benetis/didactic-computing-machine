package `me.benetis`
package testix

import me.benetis.io._

sealed trait Assert

case class AssertEqual[A](actual: IO[A], expected: IO[A])

trait AssertDsl {
  def assertEqualM[A](actual: IO[A], expected: IO[A]): IO[Boolean] = for {
    _actual <- actual
    _expected <- expected
  } yield _actual == _expected

  def assert(assertionBlock: => Boolean): IO[Boolean] = IO.effect(assertionBlock)
}
