//Some code from https://machinelearningmastery.com/implement-perceptron-algorithm-scratch-python/
//Some from Neural networks course by Ausra Saudargiene

package SingleLayerPerceptron

object Main extends App {
  def main() = {}
}

object SingleLayerPerceptron {
  def predict(row: Vector[Double], weights: Vector[Double]): Int = {
    /* bias + w0 + w1 * x1 + w2 * x2 */
    val activation = weights.head + row
      .drop(1)
      .zipWithIndex
      .foldLeft(0.0)((prev, curr: (_, Int)) =>
        prev + weights(curr._2 + 1) * row(curr._2))

    if(activation >= 0.0) 1 else 0
  }
}
