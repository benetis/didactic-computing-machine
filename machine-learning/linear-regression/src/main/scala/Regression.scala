import scalax.chart.api._

object Regression extends App {

  case class Example(name: String, target: Double, features: Double*)

  type Weight = Double

  val descentIterations = 100

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


//  val trainingSet = Vector(
//    Example("e1", 0, 0, 1),
//    Example("e2", 2, 2, 0),
//    Example("e3", 4, 2, 2),
//    Example("e4", 1, 2, 3),
//    Example("e5", 3, 5, 2)
//  )
//
//  val testSet = Vector(
//    Example("e6", 1.37, 1, 2),
//    Example("e7", 1.93, 2, 1)
//  )

  learning(
    trainingSet,
    0.001
  )

  /**
    * learn function h: X -> Y, so its a good predictor
    */
  /* Stochastic gradient descent with LMS */
  def learning(trainingSet: Vector[Example],
               alfaStep: Double): Vector[Weight] = {
    val featureAmount = trainingSet.head.features.size
    val inputThetas: Vector[Weight] = Vector
      .fill(featureAmount + 1)(1.0) //because θ_0 +θ_1x1 +θ_2x2

    def hypothesis(thetas: Vector[Weight], features: Vector[Double]): Double = {
      /* theta0 + theta1*x1 ... */
      (Vector(1.0) ++ features).zip(thetas).foldLeft(0.0) { (res, thetaTuple) =>
        {
          res + thetaTuple._1 * thetaTuple._2
        }
      }
    }

    def getXjFromFeatures(features: Vector[Double], j: Int): Double = {
      if (features.size == j) {
        1
      } else {
        features(j)
      }
    }

    def stochasticDescent(thetas: Vector[Weight],
                          examples: Vector[Example],
                          iter: Int): Vector[Weight] = {
      if (iter > 0) {
        val randomShuffledSet = scala.util.Random.shuffle(examples)

        val newThetas =
          randomShuffledSet.foldLeft(thetas)((result, curr: Example) => {
            result.zipWithIndex.map {
              case (t: Weight, j) =>
                t + alfaStep * (curr.target - hypothesis(
                  thetas,
                  curr.features.toVector)) *
                  getXjFromFeatures(curr.features.toVector, j)
            }
          })

        stochasticDescent(newThetas, examples, iter - 1)

      } else {
        thetas
      }
    }

    val resultWeights =
      stochasticDescent(inputThetas, trainingSet, descentIterations)

    val predict =
      testSet.map(ex => hypothesis(resultWeights, ex.features.toVector))



    println(s"weights $resultWeights")
    println(s"prediction $predict")

    val data = for (i <- 1 to 5) yield (i,i)
    val chart = XYLineChart(data)
    chart.show()
//    for (x <- -4.0 to 4 by 0.1) {
//   DDT4@zin
//   swing.Swing onEDT {
//        series.add(x,math.sin(x))
//      }
//      Thread.sleep(50)
//    }

    Vector(0.0)
  }

}
