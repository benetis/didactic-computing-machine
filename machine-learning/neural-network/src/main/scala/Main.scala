/* Some of the ideas taken from https://machinelearningmastery.com/implement-backpropagation-algorithm-scratch-python/ */

object Main extends App {
  //
  val nOutput      = 2 //0,1,2,3...
  val nInput       = 3
  val hiddenLayers = 1
  val learningRate = 0.05
  val iterations   = 1500
  val myNN         = NN.initializeNN(nInput, nOutput, 1)
  val finishedNN = NN.trainNetwork(
    myNN,
    Vector(
      Vector(1, 0, 1, 0),
      Vector(0.9, 0, 1, 0),
      Vector(0, 0.0, 0.0, 1),
      Vector(0, 0.2, 0.0, 1),
      Vector(0.1, 0.1, 0.0, 1)
    ),
    iterations,
    nOutput,
    learningRate
  )

  val testSet: Vector[Vector[Double]] = Vector(
    Vector(0.1, 0, 0.1, 1)
  )

  println("Trained network: ")
  finishedNN.foreach(layer => {
    layer.foreach(neuron => {
      println(neuron.weights.foreach(w => print(s"$w ")))
    })

    println()
  })

  println("Prediction start")
  testSet.foreach(trainingInstance => {
    println(
      s" Expected: ${NN.prediction(finishedNN, trainingInstance)}, actual: ${trainingInstance.last} ")
  })
}

case class Neuron(weights: Vector[Double], output: Double, errorDelta: Double)

object NN {
  type Layer = Vector[Neuron]

  val r = new scala.util.Random(1)

  private def neuronYForInputs(weights: Vector[Double],
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

  def propogateForward(
      NN: Vector[Layer],
      trainingInstance: Vector[Double]): (Vector[Layer], Vector[Double]) = {
    val updatedNN = NN.map((layer: Layer) => {
      layer.map((neuron: Neuron) => {
        val neuronLR =
          neuronYForInputs(neuron.weights, trainingInstance)
        val neuronOutput = sigmoid(neuronLR)
        neuron.copy(output = neuronOutput)
      })
    })

    (updatedNN, updatedNN.last.map(n => n.output))
  }

  def propogateBackward(NN: Vector[Layer],
                        expectedOutput: Vector[Int]): Vector[Layer] = {

    NN.zipWithIndex.map {
      case (_, ind) =>
        val i = NN.size - ind - 1
        val errors = {
          val layer = NN(i)

          if (i != NN.size - 1) {
            layer.zipWithIndex.map {
              case (_, j) =>
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

        NN(i).zipWithIndex.map {
          case (neuron: Neuron, j: Int) =>
            neuron.copy(
              errorDelta = errors(j) * sigmoidDerirative(neuron.output))
        }
    }.reverse
  }

  def updateNetworkWeights(NN: Vector[Layer],
                           trainingInstance: Vector[Double],
                           learningSpeed: Double): Vector[Layer] = {
    NN.zipWithIndex.map {
      case (layer, index) =>
        val inputs = if (index != 0) {
          NN(index - 1).map(_.output)
        } else {
          trainingInstance.dropRight(2)
        }

        layer.zipWithIndex.map {
          case (neuron: Neuron, j) =>
            val updatedNeuronWeights = inputs.zipWithIndex.map {
              case (input: Double, k: Int) =>
                neuron.weights(k) + learningSpeed * neuron.errorDelta * input
            }

            val deltaWeight = learningSpeed * neuron.errorDelta

            neuron.copy(weights = updatedNeuronWeights :+ deltaWeight)
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

  def trainNetwork(NN: Vector[Layer],
                   trainingSet: Vector[Vector[Double]],
                   iter: Int,
                   nOutput: Int,
                   learningRate: Double): Vector[Layer] = {
    if (iter < 0) NN
    else {

      var sumError = 0.0

      val nextInputNN = trainingSet.zipWithIndex.foldLeft(NN)((prev, curr) => {
        val (forward, outputs) = propogateForward(prev, curr._1)

        val expectedEmpty: Vector[Int] = Vector.fill(nOutput)(0)
        val expected                   = expectedEmpty.updated(curr._1.last.toInt, 1)

        sumError += expected.zipWithIndex.foldLeft(0.0)((prev, curr) => {
          prev + Math.pow(curr._1 - outputs(curr._2), 2)
        })

        val backward = propogateBackward(forward, expected)
        updateNetworkWeights(backward, curr._1, learningRate)
      })
      println(s"iteration: $iter, error: $sumError")

      trainNetwork(nextInputNN, trainingSet, iter - 1, nOutput, learningRate)
    }
  }

  def prediction(NN: Vector[Layer], feature: Vector[Double]) = {
    val (_, output) = propogateForward(NN, feature)
    println(s"output neurons: $output")
    output.indexOf(output.max)
  }
}
