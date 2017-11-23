package example
import example.WordFrequency.{Category, WordFreq}
import scalaz.Scalaz._

object SpamFilter extends App with DataParser {
  val data = Loader.loadData()

  println(
    NaiveBayes.classify(
    "number rate number rate number rate call".split(" ").toVector,
      WordFrequency.wordFrequencyList(data),
      data
    ))

  println(NaiveBayes.classify(
    "i am going to sleep".split(" ").toVector,
    WordFrequency.wordFrequencyList(data),
    data
  ))
}

case class TrainInst(words: Vector[String], category: String)

object NaiveBayes {

  def classify(words: Vector[String],
               catsWithWordFreq: Map[Category, WordFreq],
               data: => Vector[TrainInst]): Category = {
    val catProbs: Vector[(Category, Double, Int)] = catsWithWordFreq.map {
      case ((category, wordFreq)) =>
        val trainingExamplesOfCategory =
          data.count(_.category == category)

        val sameWords: WordFreq = wordFreq.filterKeys(c => words.contains(c))
        val diffWords           = wordFreq.filterKeys(c => !words.contains(c))

        val alpha                     = 1 / 3.toDouble
        val smoothedTrainingN: Double = trainingExamplesOfCategory + alpha * 2

        val sameWordsProb =
          sameWords.values.map(y => (y + alpha) / smoothedTrainingN.toDouble)
        val diffWordsProb = diffWords.values
          .map(x =>
            (math
              .abs(smoothedTrainingN - x) + alpha) / smoothedTrainingN.toDouble)
          .filterNot(_ == 0)

        (category,
         (sameWordsProb ++ diffWordsProb).reduce(_ + math.log(_)),
         trainingExamplesOfCategory)

    }.toVector

    val catA = catProbs.head
    val catB = catProbs.last

    val pA = catA._3.toDouble / data.size
    val pB = catB._3.toDouble / data.size

    val isCatA = catA._2 - catB._2

    val isCatB = math.log(((5 - 0) * pB) / ((1 - 0) * pA))

    if (isCatA > isCatB) catA._1
    else catB._1
  }

}

object WordFrequency {

  type WordFreq = Map[String, Int]
  type Category = String

  def wordFrequencyList(trainSet: Vector[TrainInst]): Map[String, WordFreq] = {

    trainSet
      .foldLeft(Map.empty[String, WordFreq])(
        (prev: Map[String, WordFreq], curr: TrainInst) => {
          val words: Map[String, Map[String, Int]] = curr.words
            .map(_.toLowerCase())
            .groupBy(identity)
            .mapValues(_.size)
            .mapValues(v => Map(curr.category -> v))
          prev |+| words
        })
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

  val cleanSet = ",.!?:\"#+".toSet

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
