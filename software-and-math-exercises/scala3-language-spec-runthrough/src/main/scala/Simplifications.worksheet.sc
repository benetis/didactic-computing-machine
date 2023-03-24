trait Greeting(val name: String):
  def msg = s"How are you, $name"

class C extends Greeting("Bob"):
  println(msg)

trait Ord[T]:
  def compare(x: T, y: T): Int
  extension (x: T) def <(y: T) = compare(x, y) < 0
  extension (x: T) def >(y: T) = compare(x, y) > 0

given intOrd: Ord[Int] with
  def compare(x: Int, y: Int) =
    if x < y then -1 else if x > y then +1 else 0

given listOrd[T](using ord: Ord[T]): Ord[List[T]] with

  def compare(xs: List[T], ys: List[T]): Int = (xs, ys) match
    case (Nil, Nil) => 0
    case (Nil, _)   => -1
    case (_, Nil)   => +1
    case (x :: xs1, y :: ys1) =>
      val fst = ord.compare(x, y)
      if fst != 0 then fst else compare(xs1, ys1)

given Ord[String] with
  def compare(x: String, y: String) =
    if x < y then -1 else if x > y then +1 else 0
/* Compiler will synthesize readable name if name is left out*/

// given Position = enclosingTree.position
// given (using config: Config): Factory = MemoizingFactory(config)

import scala.util.NotGiven

trait Tagged[A]

case class Foo[A](value: Boolean)
object Foo:
  given fooTagged[A](using Tagged[A]): Foo[A] = Foo(true)
  given fooNotTagged[A](using NotGiven[Tagged[A]]): Foo[A] = Foo(false)

def test(): Unit =
  given Tagged[Int]()
  assert(summon[Foo[Int]].value) // fooTagged is found
  assert(!summon[Foo[String]].value) // fooNotTagged is found

case class Circle(x: Double, y: Double, radius: Double)

extension (c: Circle) def circumference: Double = c.radius * math.Pi * 2

object Scoped {
  opaque type Logorithm = Double

  object Logorithm:
    def apply(d: Double): Logorithm = math.log(d)

  end Logorithm

  val x: Double = Logorithm(1)

}

import Scoped.Logorithm

// val y: Double = Logorithm(1)

// def doStuff(double: Double): Double = identity(double)

// doStuff(Logorithm(10))

val arr = Array(0, 1, 2, 3)
val lst = List(arr*)    
