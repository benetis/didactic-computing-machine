trait Foo:
  def foo(): Unit
  def same(): List[Foo]

trait Bar[T]:
  def bar(x: T): Unit
  def same(): List[Bar[T]]

/* Instead of compound types */
def intersection(x: Foo & Bar[String]) =
  x.foo()
  x.bar("1")

class Baz extends Foo, Bar[String] {
  def foo() = ()
  def bar(x: String) = ()

  // def same(): List[Foo] & List[Bar[String]] = List.empty
  def same(): List[Foo & Bar[String]] = List.empty
}

val x: Foo & Bar[String] = new Baz

x.foo()

trait RR:
  def run(): Unit

case class Lefty[T <: RR](err: T)
case class Righty[T <: RR](value: T)

//The compiler will assign a union type to an expression only if such a type is explicitly given

def runOrNot[T <: RR](params: Lefty[T] | Righty[T]) = params match {
  case Lefty(err)    => err.run()
  case Righty(value) => value.run()
}

runOrNot(Lefty(new RR { def run() = println("lets go") }))

trait R
trait U
trait L

type T[X] = R
type T1 = [X] =>> R
type F2[A, +B] = A => B
type F21 = [A, B] =>> A => B

type T[X] >: L <: U
type T1 >: ([X] =>> L) <: ([X] =>> U)

type T[X] <: X => X
type T >: Nothing <: ([X] =>> X => X)
// type T >: ([X] =>> Nothing) <: ([X] =>> X => X)

type F2[-A, +B]
opaque type O[X] = List[X]

//Scala 2 and 3 differ in that Scala 2 also treats Any as universal top-type. This is not done in Scala 3. See also the discussion on kind polymorphism

type Elem[X] = X match
  case String      => Char
  case Array[t]    => t
  case Iterable[t] => t

type CheckLast[T] = T match {
  case List[t] => t
  case String  => Char
}

val a: CheckLast[String] = '1'
val aList: CheckLast[List[Int]] = 1

def last[T](something: T): CheckLast[T] = something match
  case l: List[t] => l.last
  case s: String  => s.last

last(List(1, 3, 3, 7))
last("this is insa")

val list = List(3, 3, 7, 1)
val listOrd = list.sorted

given descOrd: Ordering[Int] = Ordering.fromLessThan(_ > _)
list.sorted

trait Monoid[A] {
  def combine(x: A, y: A): A
}

given intMonoid: Monoid[Int] = new Monoid[Int] {
  def combine(x: Int, y: Int): Int = x + y
}

def reduce[A](list: List[A])(using monoid: Monoid[A]): A =
  list.reduce(monoid.combine)

extension (string: String) def foo(): String = string.toLowerCase

val sum = reduce(list)

type LeafElem[X] = X match
  case String      => Char
  case Array[t]    => LeafElem[t]
  case Iterable[t] => LeafElem[t]
  case AnyVal      => X

def leafElem[X](x: X): LeafElem[X] = x match
  case x: String      => x.charAt(0)
  case x: Array[t]    => leafElem(x(0))
  case x: Iterable[t] => leafElem(x.head)
  case x: AnyVal      => x
