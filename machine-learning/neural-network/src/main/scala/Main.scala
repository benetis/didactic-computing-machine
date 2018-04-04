// Some of the ideas taken from https://machinelearningmastery.com/implement-backpropagation-algorithm-scratch-python/
// Other code implemented according to Gailius Raskinis Machine learning course

object Main extends App {
  //
  val nOutputClasses = 2
  val nFeatures = 1
  val nHiddenLayers = 1
  val hiddenLayerNeuronN = 5
  val learningRate = 0.5
  val iterations = 500
  val myNN = NN.initializeNN(nFeatures, nOutputClasses, nHiddenLayers, hiddenLayerNeuronN)
  val finishedNN = NN.trainNetwork(
    myNN,
    Vector(
      Vector(0.0, 0),
      Vector(0.99, 1),
      Vector(0.99, 1),
      Vector(0.0, 0),
      Vector(0.0, 0),
      Vector(0.99, 1)
    ),
    iterations,
    nOutputClasses,
    learningRate
  )

  val testSet: Vector[Vector[Double]] = Vector(
    Vector(0.0, 0),
    Vector(0.99, 1)
  )

  println("Trained network: ")
  finishedNN.zipWithIndex.foreach { case (layer, i) =>
    println(s"Layer[$i]")
    layer.foreach(neuron => {
      println(neuron.weights.foreach(w => print(s"$w ")))
    })
    println()
  }

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

  def propogateForward(
    neuralNet: Vector[Layer],
    trainingInstance: Vector[Double]): (Vector[Layer], Vector[Double]) = {

    def propagate(NN: Vector[Layer], lastLayerOutputs: Vector[Double], nth: Int): Vector[Layer] = {
      def layerActivation(layer: Layer) = (lastLayerInputs: Vector[Double]) => {
        layer.map((neuron: Neuron) => {
          val neuronLR =
            neuronActivation(neuron.weights, lastLayerInputs)
          val neuronOutput = sigmoid(neuronLR)
          neuron.copy(output = neuronOutput)
        })
      }

      if(nth == NN.length) NN else {
        val updatedLayer: Layer = nth match {
          case 0 => layerActivation(NN(nth))(trainingInstance)/* activate from training */
          case _ => layerActivation(NN(nth))(lastLayerOutputs)
        }

        propagate(NN.updated(nth, updatedLayer), NN(nth).map(_.output), nth + 1)
      }
    }

    val updatedNN = propagate(neuralNet, trainingInstance, 0)

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
  def initializeNN(
    nFeatures: Int,
    nOutputClasses: Int,
    nHiddenLayer: Int,
    hiddenLayerNeuronN: Int): Vector[Layer] = {

    def layer(neuronAmount: Int, prevLayerNeuronAmount: Int): Layer = {
//      val biasInput = 2
      Vector.fill(neuronAmount) {
        val weights = Vector.fill(prevLayerNeuronAmount) {
          r.nextDouble()
        }
        Neuron(weights, 0.0, 0.0)
      }
    }

    val inputLayer: Layer = layer(nFeatures, nFeatures)

    val hiddenLayers: Vector[Layer] = (1 to nHiddenLayer)
      .map {
        case 1 => layer(hiddenLayerNeuronN, nFeatures)
        case _ => layer(hiddenLayerNeuronN, hiddenLayerNeuronN)
      }.toVector

    val outputLayer: Layer = layer(nOutputClasses, hiddenLayerNeuronN)

    inputLayer +: hiddenLayers :+ outputLayer
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
        val expected = expectedEmpty.updated(curr._1.last.toInt, 1)

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
