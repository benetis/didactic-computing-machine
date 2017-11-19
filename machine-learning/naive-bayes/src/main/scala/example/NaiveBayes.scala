package example
//import breeze.linalg._
import breeze.linalg.DenseMatrix
import scalaz.Scalaz._

object SpamFilter extends App {
  val input = Vector(
    TrainInst(Vector("test", "testas"), "test"),
    TrainInst(Vector("00", "000"), "00"),
    TrainInst(Vector("00", "0000"), "00")
  )
}

case class TrainInst(words: Vector[String], category: String)

object NaiveBayes {

  val lossMatrix = DenseMatrix((0, 10), (1, 0))

}

object WordFrequency {

  type WordFreq = Map[String, Int]
  type Category = String

  def splitCategoriesWithFrequencies(trainingSet: Vector[TrainInst]): Map[Category, WordFreq] = {
    val categoryMaps = splitIntoCategoryMaps(trainingSet)
    categoryMaps.mapValues(wordFrequencyList)
  }

  def wordFrequencyList(wordsList: Vector[String]): WordFreq = {
    wordsList
      .groupBy(identity).mapValues(_.size)
      .foldLeft(Map.empty[String, Int])((prev: WordFreq, curr) => {
        val word = curr._1
        val freq = curr._2
        prev |+| Map(word -> freq)
      })
  }

  def splitIntoCategoryMaps(
    trainingSet: Vector[TrainInst]): Map[String, Vector[String]] = {
    trainingSet.foldLeft(Map.empty[String, Vector[String]])(
      (prev, curr: TrainInst) =>
        prev |+| Map(curr.category -> curr.words))
  }
}
