package SingleLayerPerceptron

import org.scalatest._

class Test extends FreeSpec with Matchers {

  "Predict" - {
    /* Two inputs + bias */
    val weights = Vector(-0.1, 0.09000000000000001, 0.07)

    "Predict should return class 0 with this row and weights" in {

      val row1 = Vector(0.0, 0.1, 0)
      val row2 = Vector(0.2, 0.1, 0)

      SingleLayerPerceptron.predict(row1, weights) shouldEqual 0
      SingleLayerPerceptron.predict(row2, weights) shouldEqual 0
    }


    "Predict should return class 1 with this row and weights" in {
      val row3 = Vector(0.9, 0.9, 1)
      val row4 = Vector(0.7, 0.8, 1)

      SingleLayerPerceptron.predict(row3, weights) shouldEqual 1
      SingleLayerPerceptron.predict(row4, weights) shouldEqual 1
    }

  }

  "Train" - {


    "Train network should output weights given dataset" in {

      val rows = Vector(
        Vector(0.0, 0.1, 0),
        Vector(0.2, 0.1, 0),
        Vector(0.9, 0.9, 1),
        Vector(0.7, 0.8, 1)
      )

      val learningRate = 0.1
      val iterations = 500

      SingleLayerPerceptron.trainNetwork(
        rows, learningRate, iterations) shouldEqual Vector(-0.1, 0.09000000000000001, 0.07)

    }

    "Train network and prediction should pass test dataset" in {
      val train = Vector(
        Vector(0.0, 0.1, 0),
        Vector(0.2, 0.1, 0),
        Vector(0.9, 0.9, 1),
        Vector(0.1, 0.8, 1),
        Vector(0.3, 0.1, 0)
      )

      val test = Vector(
        Vector(0.1, 0.15, 0),
        Vector(0.9, 0.75, 1)
      )

      val weights = SingleLayerPerceptron.trainNetwork(train, 0.1, 500)
      val predicted = test.map(r => SingleLayerPerceptron.predict(r, weights) -> r.last)

      predicted.head._1 shouldEqual predicted.head._2.toInt
      predicted.last._1 shouldEqual predicted.last._2.toInt

    }
  }

}
