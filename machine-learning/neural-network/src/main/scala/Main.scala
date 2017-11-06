/* Some of the ideas taken from https://machinelearningmastery.com/implement-backpropagation-algorithm-scratch-python/ */

object Main extends App {

  val myNN = NN.initializeNN(2, 2, 1)

  println(NN.propogateForward(myNN, Vector(1, 0)))
}

object NN {
  type Layer = Vector[Vector[Double]]

  val r = new scala.util.Random(1)

  private def linearRegressionForNeuron(weights: Vector[Double], inputs: Vector[Double]): Double = {
    weights.dropRight(1).zipWithIndex.foldLeft(weights.last)((prev, curr) => {
      val index: Int = curr._2
      prev + weights(index) + inputs(index)
    })
  }

  private def sigmoid(neuronWeightSum: Double): Double = {
    1.0 / (1.0 + math.exp(-neuronWeightSum))
  }

  def propogateForward(NN: Vector[Layer], trainingSet: Vector[Double]): Vector[Double] = {
    NN.foldLeft(Vector(0.0))((prev, layer: Layer) => layer.map((neuron: Vector[Double]) => {
      val neuronLR = linearRegressionForNeuron(neuron, trainingSet)
      val neuronOutput = sigmoid(neuronLR)
      neuronOutput
    }))
  }

  /** Neuron amount in layers as arguments **/
  def initializeNN(nInputs: Int, nOutputs: Int, nHidden: Int): Vector[Layer] = {

    def layer(neuronsInLayer: Int, prevLayerNeuronN: Int) = {
      val biasInput = 1
      Vector.fill(neuronsInLayer) {
        Vector.fill(prevLayerNeuronN + biasInput) { r.nextDouble() }
      }
    }

    val hiddenLayer = layer(nHidden, nInputs)

    val outputLayer = layer(nOutputs, nHidden)

    Vector(hiddenLayer, outputLayer)
  }
}