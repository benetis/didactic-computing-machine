import scala.collection.immutable
object Regression extends App {

  case class Example(name: String, target: Double, features: Double*)

  type Weight = Double

  val descentIterations = 10000

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

//  val trainingSet = Vector(
//    Example("e1", 0, 0, 1),
//    Example("e2", 2, 2, 0),
//    Example("e3", 4, 2, 2),
//    Example("e4", 1, 2, 3),
//    Example("e5", 3, 5, 2)
//  )

  val testSet = Vector(
    Example("e9", -9, -3, 3, 4, 4),
    Example("e10", -2.5, 0, 0, -5, -4)
  )

//  val testSet = Vector(
//    Example("e6", 1.37, 1, 2),
//    Example("e7", 1.93, 2, 1)
//  )

  learning(
    trainingSet,
    0.00001
  )

  /**
    * learn function h: X -> Y, so its a good predictor
    */
  /* Stochastic gradient descent with LMS */
  def learning(trainingSet: Vector[Example],
               alfaStep: Double): Vector[Weight] = {
    val featureAmount = trainingSet.head.features.size

    def linear(weightFeature: (Weight, Double)): Double =
      weightFeature._1 * weightFeature._2

    def linearAdditional(weightFeature: (Weight, Double)): Double =
      linear(weightFeature) * weightFeature._2

    def hypothesis(thetas: Vector[Weight],
                   features: Vector[Double],
                   func: ((Weight, Double)) => Double): Double = {
      /* theta0 + theta1*x1 ... */
      features.updated(0, 1.0).zip(thetas).foldLeft(0.0) { (res, thetaTuple) =>
        {
          res + func(thetaTuple._1, thetaTuple._2)
        }
      }
    }

    val inputThetas: Vector[Weight] = Vector
      .fill(featureAmount)(1.0)

    def stochasticDescent(thetas: Vector[Weight],
                          examples: Vector[Example],
                          iter: Int) : Vector[Weight] = {
      if (iter > 0) {
        val randomShuffledSet = scala.util.Random.shuffle(examples)

        val newThetas = randomShuffledSet.foldLeft(thetas)(
          (result, curr: Example) => {
            result.zipWithIndex.map {
              case (t: Weight, i) =>
                t - alfaStep * hypothesis(thetas,
                                          curr.features.toVector,
                                          linear) * curr.features(i)
            }
          })

        stochasticDescent(newThetas, examples, iter - 1)

      } else {
        thetas
      }
    }

    val resultWeights = stochasticDescent(inputThetas, trainingSet, descentIterations)

    val predict =
      testSet.map(ex => hypothesis(resultWeights, ex.features.toVector, linear))

    val predictNonLinear =
      testSet.map(ex =>
        hypothesis(resultWeights, ex.features.toVector, linearAdditional))

    println(s"weights $resultWeights")
    println(s"prediction $predict")
    println(s"prediction non linear $predictNonLinear")

    Vector(0.0)
  }

}
