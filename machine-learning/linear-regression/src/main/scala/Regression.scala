import scala.collection.immutable
object Regression extends App {

  case class Example(name: String, target: Double, features: Double*)

  type Weight = Double

  val epsilon = 0.001

  val trainingSet = Vector(
    Example("e1", -14.5, -2, 4, -3, -4),
    Example("e2", 5, 2, -4, 0, 2),
    Example("e3", -17.5, -5, 3, -3, 0),
    Example("e4", -3.5, -1, 1, 1, -2),
    Example("e5", 0.5, 3, -2, -2, -1),
    Example("e6", 11, -1, -5, 0, 5),
    Example("e7", 10.5, -1, -5, -5, -3),
    Example("e8", -0.5, -2, -1, -3, -3)
  )

  val testSet = Vector(
    Example("e9", -9, -3, 3, 4, 4),
    Example("e10", -2.5, 0, 0, -5, -4)
  )

  learning(
    trainingSet,
    0.1
  )

  /**
    * learn function h: X -> Y, so its a good predictor
    */
  /* Stochastic gradient descent with LMS */
  def learning(trainingSet: Vector[Example],
               alfaStep: Double): Vector[Weight] = {
    val featureAmount = trainingSet.head.features.size

    def hypothesis(thetas: Vector[Weight], features: Vector[Double]): Double = {
      /* theta0 + theta1*x1 ... */
      features.updated(0, 1.0).zip(thetas).foldLeft(0.0) { (res, thetaTuple) =>
        {
          res + thetaTuple._1 * thetaTuple._2
        }
      }
    }

    def gradientDescentForWeight(
        epsilon: Double,
        alfaStep: Double,
        thetas: Vector[Weight],
        features: Vector[Double],
        startingValue: Double,
        previousValue: Double,
        fun: (Double) => Double
    ): Double = {
      if (math.abs(startingValue - previousValue) > epsilon) { //Converges
        gradientDescentForWeight(
          epsilon,
          alfaStep,
          thetas,
          features,
          startingValue - alfaStep * fun(startingValue),
          startingValue,
          fun
        )
      } else
        startingValue
    }

    val inputThetas: Vector[Weight] = Vector
      .fill(featureAmount)(math.random())

    val result = trainingSet.map((curr: Example) => {

      //visiems svoriams paskaiciuoti naujus svorius pagal sita example

      def calculateThetas(
          currThetas: Vector[Weight],
          remainingThetas: Vector[(Weight, Int)]): Vector[Weight] = {

        if (remainingThetas.nonEmpty) { //Calculate next one

          val currTheta: (Weight, Int) = remainingThetas.head

          val nextTheta = gradientDescentForWeight(
            epsilon,
            alfaStep,
            currThetas,
            curr.features.toVector,
            currTheta._1,
            -1.0,
            (h: Double) => (curr.target - h) * curr.features(currTheta._2)
          )

          calculateThetas(Vector(nextTheta) ++ currThetas.tail, remainingThetas.tail)

        } else {
          currThetas
        }

      }

      val th: Vector[Weight]         = inputThetas
      val thi: Vector[(Weight, Int)] = inputThetas.zipWithIndex

      calculateThetas(th, thi)

    })

    val predict = testSet.map(ex => hypothesis(result.last, ex.features.toVector))

    println(s"weights ${result.last}")
    println(s"prediction $predict")

    result.last

  }

}
