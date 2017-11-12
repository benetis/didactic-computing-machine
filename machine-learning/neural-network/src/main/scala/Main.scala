// Some of the ideas taken from https://machinelearningmastery.com/implement-backpropagation-algorithm-scratch-python/
// Other code implemented according to Gailius Raskinis Machine learning course
object Main extends App {
  //
  val nOutput      = 2 //0,1,2,3...
  val nInput       = 2
  val hiddenLayers = 1
  val learningRate = 0.5
  val iterations   = 500
  val myNN         = NN.initializeNN(nInput, nOutput, hiddenLayers)
  val finishedNN = NN.trainNetwork(
    myNN,
    Vector(
      Vector(0.0, 0.0, 0),
      Vector(0.99, 0.89, 1),
      Vector(0.99, 0.89, 1),
      Vector(0.0, 0.0, 0),
      Vector(0.0, 0.0, 0),
      Vector(0.99, 0.89, 1)
    ),
    iterations,
    nOutput,
    learningRate
  )

  val testSet: Vector[Vector[Double]] = Vector(
    Vector(0.0, 0.1, 0),
    Vector(0.99, 0.8, 1)
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

  private def neuronActivation(weights: Vector[Double],
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
          neuronActivation(neuron.weights, trainingInstance)
        val neuronOutput = sigmoid(neuronLR)
        neuron.copy(output = neuronOutput)
      })
    })

    (updatedNN, updatedNN.last.map(n => n.output))
  }

  def propogateBackward(NN: Vector[Layer], T: Vector[Int]): Vector[Layer] = {
    val lastLayerWithSigma: Layer = NN.last.zip(T).map {
      case (neuron: Neuron, expectedNeuronOutput: Int) =>
        //sigma_last_k = y_last_k * (1 - y_last_k)(T_k - y_last_k)
        neuron
          .copy(
            errorDelta = neuron.output * (1 - neuron.output) * (expectedNeuronOutput - neuron.output))
    }

    def sumOfLayerErrors(layerIndex: Int, neuronIndex: Int): Double = {

      def reverseLayerIndex(i: Int) = math.abs(i - NN.length + 1)

      NN(reverseLayerIndex(layerIndex)).foldLeft(0.0)((prev, curr) => {
        prev + curr.errorDelta * curr.weights(neuronIndex)
      })
    }

    NN.updated(NN.length - 1, lastLayerWithSigma)
      .reverse
      .zipWithIndex
      .map {
        case (layer: Layer, index: Int) =>
          if (index == 0) layer
          else {

            val previousLayer = index - 1 //Because we iterate backwards

            layer.zipWithIndex.map {
              case (neuron, neuronIndex) =>
                neuron.copy(
                  errorDelta =
                    neuron.output * (1 - neuron.output) * sumOfLayerErrors(
                      previousLayer,
                      neuronIndex))
            }
          }
      }
      .reverse

  }

  def updateNetworkWeights(NN: Vector[Layer],
                           trainingInstance: Vector[Double],
                           learningRate: Double): Vector[Layer] = {

    val firstLayer: Layer = NN.head.zipWithIndex.map {
      case (neuron: Neuron, index) =>
        val newW = neuron.weights(index) + learningRate * trainingInstance(
          index)
        neuron.copy(weights = neuron.weights.updated(index, newW))
    }

    val hiddenLayers: Vector[Layer] = NN.drop(1).map { layer =>
      layer.zipWithIndex.map {
        case (neuron, index) =>
          val newW = neuron
            .weights(index) + learningRate * neuron.errorDelta * NN(
            NN.length - 2)(index).output
          neuron.copy(weights = neuron.weights.updated(index, newW))
      }
    }

    firstLayer +: hiddenLayers
  }

  /** Neuron amount in layers as arguments **/
  def initializeNN(nInputs: Int, nOutputs: Int, nHidden: Int): Vector[Layer] = {

    def layer(neuronAmount: Int, prevLayerNeuronAmount: Int): Layer = {
//      val biasInput = 2
      Vector.fill(neuronAmount) {
        val weights = Vector.fill(prevLayerNeuronAmount) {
          r.nextDouble()
        }
        Neuron(weights, 0.0, 0.0)
      }
    }

    val hiddenLayers: Vector[Layer] = (0 to nHidden).map(_ => layer(nInputs, nInputs * 2)).toVector
    val outputLayer: Layer = layer(nInputs, nInputs * 2)

    hiddenLayers :+ outputLayer
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
