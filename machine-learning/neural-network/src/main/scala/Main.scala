// Some of the ideas taken from https://machinelearningmastery.com/implement-backpropagation-algorithm-scratch-python/
// Other code implemented according to Gailius Raskinis Machine learning course

object Main extends App {
  //
  val nOutputClasses = 2
  val nFeatures = 2
  val nHiddenLayers = 1
  val hiddenLayerNeuronN = 5
  val learningRate = 0.5
  val iterations = 100
  val myNN = NN.initializeNN(nFeatures, nOutputClasses, nHiddenLayers, hiddenLayerNeuronN)
  val finishedNN = NN.trainNetwork(
    myNN,
    Vector(
      Vector(0.91, 0.92, 1),
      Vector(0.93, 0.94, 1),
      Vector(0.01, 0.02, 0),
      Vector(0.03, 0.04, 0)
    ),
    iterations,
    nOutputClasses,
    learningRate
  )

  val testSet: Vector[Vector[Double]] = Vector(
    Vector(0.93, 0.94, 1),
    Vector(0.01, 0.02, 0)
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

  def propagateForward(
    neuralNet: Vector[Layer],
    trainingInstance: Vector[Double]): (Vector[Layer], Vector[Double]) = {

    def propagate(NN: Vector[Layer], lastLayerOutputs: Vector[Double], nth: Int): Vector[Layer] = {
      def layerActivation(layer: Layer, lastLayerInputs: Vector[Double]) = {
        layer.map((neuron: Neuron) => {
          val neuronLR =
            neuronActivation(neuron.weights, lastLayerInputs)
          val neuronOutput = sigmoid(neuronLR)
          neuron.copy(output = neuronOutput)
        })
      }

      if (nth == NN.length) NN else {

        val updLayer: Layer = nth match {
          case 0 => layerActivation(NN(nth), trainingInstance)/* activate from training */
          case _ => layerActivation(NN(nth), lastLayerOutputs)
        }

        propagate(NN.updated(nth, updLayer), updLayer.map(_.output), nth + 1)
      }
    }

    val updatedNN = propagate(neuralNet, trainingInstance, 0)

    (updatedNN, updatedNN.last.map(n => n.output))
  }

  def propogateBackward(neuralNet: Vector[Layer], expected: Vector[Int]): Vector[Layer] = {

    def sigmoidDerivative(output: Double): Double = output * (1.0 - output)

    def backward(NN: Vector[Layer], nth: Int): Vector[Layer] = {

      if (nth == 0) NN else {
        val currentLayer = NN(nth)

        val errors: Vector[Double] = if (nth != NN.length - 1) {/* Last element in reversed layers list */
          currentLayer.zipWithIndex.map { case (_, i: Int) =>
            NN(nth + 1).foldLeft(0.0)((prev, curr) => {
              prev + curr.weights(i) * curr.errorDelta
            })
          }
        } else {
          currentLayer.zipWithIndex.map { case (_, i: Int) =>
            val neuron = currentLayer(i)
            expected(i) - neuron.output
          }
        }

        val updatedDeltas: Vector[Double] = currentLayer.zipWithIndex.map { case (_, i: Int) =>
          val neuron = currentLayer(i)

          errors(i) * sigmoidDerivative(neuron.output)
        }

        val updatedLayer = currentLayer.zip(updatedDeltas).map { case (neuron: Neuron, errDelt: Double) => {
          neuron.copy(errorDelta = errDelt)
        }
        }

        val updatedNN = NN.updated(nth, updatedLayer)

        backward(updatedNN, nth - 1)
      }
    }

    backward(neuralNet, neuralNet.length - 1)

  }

  def updateNetworkWeights(neuralNet: Vector[Layer],
    trainingInstance: Vector[Double],
    learningRate: Double): Vector[Layer] = {

    def updateWeights(NN: Vector[Layer], nth: Int): Vector[Layer] = {

      if (nth == NN.length) NN
      else {
        val inputs = nth match {
          case 0 => trainingInstance
          case _ => neuralNet(nth - 1).map(_.output)
        }

        val updatedLayer = NN(nth).map { case (neuron: Neuron) =>
          val inputUpdatedWeights = inputs.map(_ * neuron.errorDelta * learningRate)
          val lastWeight = learningRate * neuron.errorDelta

          val updatedWeights: Vector[Double] = neuron.weights zip inputUpdatedWeights map { case (a, b) => a + b }

          val withLastWeight = updatedWeights
            .updated(updatedWeights.length - 1, updatedWeights.length - 1 + lastWeight)

          neuron.copy(weights = withLastWeight)
        }

        updateWeights(NN.updated(nth, updatedLayer), nth + 1)
      }

    }

    updateWeights(neuralNet, 0)
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
        case 1 => layer(hiddenLayerNeuronN, nFeatures + 1)
        case _ => layer(hiddenLayerNeuronN, hiddenLayerNeuronN + 1)
      }.toVector

    val outputLayer: Layer = layer(nOutputClasses, hiddenLayerNeuronN)

    inputLayer +: hiddenLayers :+ outputLayer
  }

  def trainNetwork(neuralNet: Vector[Layer],
    trainingSet: Vector[Vector[Double]],
    iter: Int,
    nOutput: Int,
    learningRate: Double): Vector[Layer] = {
    if (iter < 0) neuralNet
    else {

      var sumError = 0.0

      def trainRow(NN: Vector[Layer], setToTrainOn: Vector[Vector[Double]]): Vector[Layer] = {
        if (setToTrainOn.isEmpty) NN else {
          val trainingRow = setToTrainOn.head
          val (forwardNetwork, outputs) = propagateForward(NN, trainingRow)
          val expectedEmpty: Vector[Int] = Vector.fill(nOutput)(0)
          val expected = expectedEmpty.updated(trainingRow.last.toInt, 1)

          sumError += expected.zipWithIndex.foldLeft(0.0)((prev, curr) => {
            prev + Math.pow(expected(curr._2) - outputs(curr._2), 2)
          })

          val backward = propogateBackward(forwardNetwork, expected)

          val updatedNN = updateNetworkWeights(backward, trainingRow, learningRate)

          trainRow(updatedNN, setToTrainOn.tail)
        }
      }

      val nextInputNN = trainRow(neuralNet, trainingSet)

      println(s"iteration: $iter, error: $sumError")

      trainNetwork(nextInputNN, trainingSet, iter - 1, nOutput, learningRate)
    }
  }

  def prediction(NN: Vector[Layer], feature: Vector[Double]) = {
    val (_, output) = propagateForward(NN, feature)
    println(s"output neurons: $output")
    output.indexOf(output.max)
  }
}
