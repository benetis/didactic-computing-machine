/* Some of the ideas taken from https://machinelearningmastery.com/implement-backpropagation-algorithm-scratch-python/ */

object Main extends App {

  val myNN = NN.initializeNN(2, 2, 1)

  val (updatedNN, _) = NN.propogateForward(myNN, Vector(1, 0))
  println(NN.propogateBackward(updatedNN, Vector(1, 0)))
}

case class Neuron(weights: Vector[Double], output: Double, errorDelta: Double)

object NN {
  type Layer = Vector[Neuron]

  val r = new scala.util.Random(1)

  private def linearRegressionForNeuron(weights: Vector[Double],
                                        inputs: Vector[Double]): Double = {
    weights
      .dropRight(1)
      .zipWithIndex
      .foldLeft(weights.last)((prev, curr) => {
        val index: Int = curr._2
        prev + weights(index) + inputs(index)
      })
  }

  private def sigmoid(neuronWeightSum: Double): Double = {
    1.0 / (1.0 + math.exp(-neuronWeightSum))
  }

  private def sigmoidDerirative(x: Double): Double = {
    x * (1.0 - x)
  }

  def propogateForward(NN: Vector[Layer],
                       trainingSet: Vector[Double]): (Vector[Layer], Vector[Double]) = {
    val updatedNN = NN.map((layer: Layer) => {
      layer.map((neuron: Neuron) => {
        val neuronLR     = linearRegressionForNeuron(neuron.weights, trainingSet)
        val neuronOutput = sigmoid(neuronLR)
        neuron.copy(output = neuronOutput)
      })
    })

    (updatedNN, updatedNN.last.map(_.output))
  }

  def propogateBackward(NN: Vector[Layer],
                        expectedOutput: Vector[Double]): Vector[Layer] = {
    NN.zipWithIndex.map {
      case (layer: Layer, i) =>
        val errors = {
          if (layer != NN.last) {

            layer.zipWithIndex.map {
              case (_, j: Int) =>
                NN(i + 1).foldLeft(0.0)((prev, neuron: Neuron) => {
                  prev + neuron.weights(j) * neuron.errorDelta
                })
            }
          } else {

            layer.zipWithIndex.map {
              case (_, j: Int) =>
                expectedOutput(j) - layer(j).output
            }
          }
        }

        layer.zipWithIndex.map {
          case (neuron: Neuron, j: Int) =>
            neuron.copy(
              errorDelta = errors(j) * sigmoidDerirative(neuron.output))
        }
    }
  }

  /** Neuron amount in layers as arguments **/
  def initializeNN(nInputs: Int, nOutputs: Int, nHidden: Int): Vector[Layer] = {

    def layer(neuronsInLayer: Int, prevLayerNeuronN: Int): Layer = {
      val biasInput = 1
      Vector.fill(neuronsInLayer) {
        val neuronWeights = Vector.fill(prevLayerNeuronN + biasInput) {
          r.nextDouble()
        }
        Neuron(neuronWeights, 0.0, 0.0)
      }
    }

    val hiddenLayer = layer(nHidden, nInputs)

    val outputLayer = layer(nOutputs, nHidden)

    Vector(hiddenLayer, outputLayer)
  }
}
