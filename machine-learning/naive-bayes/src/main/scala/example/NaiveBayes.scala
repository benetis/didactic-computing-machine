package example
//import breeze.linalg._
import breeze.linalg.DenseMatrix
import example.WordFrequency.{Category, WordFreq}
import scalaz.Scalaz._

object SpamFilter extends App with DataParser {
  val data     = Loader.loadData()

  val catsWithFreqs = WordFrequency.splitCategoriesWithFrequencies(data)
  println(catsWithFreqs)
}

case class TrainInst(words: Vector[String], category: String)

object NaiveBayes {

  val lossMatrix = DenseMatrix((0, 10), (1, 0))

  def classify(words: Vector[String],
               catsWithWordFreq: Map[Category, WordFreq], data: => Vector[TrainInst]): (String, Double) = {
    val catProbs = catsWithWordFreq.mapValues((category: WordFreq) => {

      val catN = data.count(_.category == category)

      val sameWords: WordFreq = category.filterKeys(c => words.contains(c))
      val diffWords = category.filterKeys(c => !words.contains(c))


      val sameWordsProb = sameWords.values
      val diffWordsProb = diffWords.values.map(x => math.abs(x - catN))

      (sameWordsProb ++ diffWordsProb).product / math.pow(catN, data.size)

    })

    catProbs.max

  }

}

object WordFrequency {

  type WordFreq = Map[String, Int]
  type Category = String

  def splitCategoriesWithFrequencies(
      trainingSet: Vector[TrainInst]): Map[Category, WordFreq] = {
    val categoryMaps = splitIntoCategoryMaps(trainingSet)
    categoryMaps.mapValues(wordFrequencyList)
  }

  def wordFrequencyList(wordsList: Vector[String]): WordFreq = {
    wordsList
      .groupBy(identity)
      .mapValues(_.size)
      .foldLeft(Map.empty[String, Int])((prev: WordFreq, curr) => {
        val word = curr._1
        val freq = curr._2
        prev |+| Map(word -> freq)
      })
  }

  def splitIntoCategoryMaps(
      trainingSet: Vector[TrainInst]): Map[String, Vector[String]] = {
    trainingSet.foldLeft(Map.empty[String, Vector[String]])(
      (prev, curr: TrainInst) => prev |+| Map(curr.category -> curr.words))
  }
}

object Loader extends DataParser {

  def loadData(): Vector[TrainInst] = {
    val bufferedSource = io.Source.fromResource("./data.csv")
    val data           = bufferedSource.getLines.drop(1).map(trimLine).toVector
    bufferedSource.close
    data
  }
}

trait DataParser {
  val spam    = "spam"
  val notSpam = "ham"

  val cleanSet = ",.!?:".toSet

  def trimLine(line: String): TrainInst = {

    def buildInstance(cat: String): TrainInst = {
      TrainInst(line
                  .drop(cat.length + 1)
                  .trim
                  .split(" ")
                  .map(_.filterNot(cleanSet))
                  .toVector,
                cat)
    }

    if (line.charAt(0) == spam.charAt(0)) {
      buildInstance(spam)
    } else {
      buildInstance(notSpam)
    }
  }
}
