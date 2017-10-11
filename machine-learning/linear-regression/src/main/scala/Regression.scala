object Regression extends App {

  case class Example(name: String, target: Double, features: Double*)

  val trainingSet = Vector(
    Example("e1", -14.5, -2, 4, -3, -4),
    Example("e2", 5, 2, -4, 0, 2),
    Example("e3", -17.5, -5, 3, -3, 0),
    Example("e4", -3.5, -1, 1, 1, -2),
    Example("e5", 0.5, 3, -2, -2, -1),
    Example("e6", 11, -1, -5, 0, 5),
    Example("e7", 10.5, -1, -5, -5, -3),
    Example("e8", -0.5, -2, -1, -3, -3),
    Example("e9", -9, -3, 3, 4, 4),
    Example("e10", -2.5, 0, 0, -5, -4)
  )


  /* Least mean squares */
  def error() = {

  }


}
