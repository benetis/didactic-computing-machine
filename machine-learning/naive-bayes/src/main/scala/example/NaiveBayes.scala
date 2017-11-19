package example
//import breeze.linalg._
import breeze.linalg.DenseMatrix
import scalaz._
import Scalaz._

object SpamFilter extends App {
  val input = Vector(
    TrainingInstance(Vector("test", "testas"), "test"),
    TrainingInstance(Vector("00", "000"), "00"),
    TrainingInstance(Vector("00", "0000"), "00")
  )
}

case class TrainingInstance(words: Vector[String], category: String)

object NaiveBayes {

  val lossMatrix = DenseMatrix((0, 10), (1, 0))

  def wordFrequencyList(
      trainingSet: Vector[TrainingInstance]): Map[String, Int] = {
    trainingSet
      .flatMap(_.words.groupBy(identity).mapValues(_.size))
      .foldLeft(Map.empty[String, Int])((prev: Map[String, Int], curr) => {
        val word = curr._1
        val freq = curr._2
        val updated = prev |+| Map(word -> freq)
        updated
      })
  }
}
