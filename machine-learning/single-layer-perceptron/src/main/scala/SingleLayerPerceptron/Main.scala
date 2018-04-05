//Some code from https://machinelearningmastery.com/implement-perceptron-algorithm-scratch-python/
//Some from Neural networks course by Ausra Saudargiene

package SingleLayerPerceptron

import scala.io.Source

object Main extends App {

  def readData(): Vector[Vector[Double]] = {
    val src: Iterator[String] = Source.fromFile("src/data.txt").getLines

    val splitLines: Iterator[Array[String]] = src.map(_.split(","))

    def lineToDoubles(line: Array[String]): Vector[Double] = {
      val className: Double = if (line.last == "R") 1 else 0
      val features = line.dropRight(1).map(_.toDouble)
      features.toVector :+ className
    }

    splitLines.map(lineToDoubles).toVector
  }

  val allData = readData()

  val trainingData = allData.dropRight((allData.length * 0.2).toInt)
  val testData = allData.drop((allData.length * 0.8).toInt)

  def predict(learningRate: Double, iterations: Int): Unit = {
    val weights = SingleLayerPerceptron.trainNetwork(trainingData, learningRate, iterations)
    val predictions = testData.map(r => SingleLayerPerceptron.predict(r, weights) -> r.last)

    val howManyPredictedCorrect: Double = predictions.map(c => c._1 == c._2.toInt).count(_ == true)

    val meanAccuracy: Double = howManyPredictedCorrect / predictions.length

//    println(weights)
//    println(meanAccuracy * 100)
    println(meanAccuracy * 100)
  }

  predict(0.1, 10)
  predict(0.5, 10)
  predict(1, 10)

  predict(0.1, 50)
  predict(0.5, 50)
  predict(1, 50)

  predict(0.1, 500)
  predict(0.5, 500)
  predict(1, 500)

  predict(0.1, 5000)
  predict(0.01, 10000)

}

object SingleLayerPerceptron {
  def predict(row: Vector[Double], weights: Vector[Double]): Int = {
    /* bias + w0 + w1 * x1 + w2 * x2 */
    val activation = weights.head + row
      .drop(1)
      .zipWithIndex
      .foldLeft(0.0)((prev, curr: (_, Int)) =>
        prev + weights(curr._2 + 1) * row(curr._2))

    if (activation >= 0.0) 1 else 0
  }

  def trainNetwork(
    rows: Vector[Vector[Double]],
    learningRate: Double,
    iterations: Int): Vector[Double] = {

    def trainWeights(weights: Vector[Double], nth: Int): Vector[Double] = {
      if (nth == 0) weights else {

        var sumError = 0.0

        def trainFromSet(
          weights: Vector[Double],
          rows: Vector[Vector[Double]]): Vector[Double] = {
          if (rows.isEmpty) weights else {
            val row = rows.head
            val prediction = predict(row, weights)
            val error = row.last - prediction
            sumError += Math.pow(error, 2)

            val biasWeight: Double = weights(0) + learningRate * error
            val updatedWeights = biasWeight +: row.drop(1).zipWithIndex.map { case (_, i) =>
              weights(i + 1) + learningRate * error * row(i)
            }
//            println(s"iteration: $nth, error: $sumError")

            trainFromSet(updatedWeights, rows.tail)
          }
        }

        val updatedWeights = trainFromSet(weights, rows)

        trainWeights(updatedWeights, nth - 1)
      }
    }

    trainWeights(Vector.fill(rows.head.length)(0), iterations)
  }
}
