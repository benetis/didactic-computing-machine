package example
import example.WordFrequency.{Category, WordFreq}
import scalaz.Scalaz._
import scalaz.Semigroup

object SpamFilter extends App with DataParser {
  val data = Loader.loadData()

  val testSentences: Vector[(String, String)] = Vector(
    ("rate rate rate", "spam"),
    ("i am going to sleep", "ham"),
    ("urgent discount", "spam"),
    ("i will call you later", "ham"),
    ("call our customer service", "spam")
  )

  testSentences.foreach((curr: (String, String)) => {
    val actual = NaiveBayes.classify(
      curr._1.split(" ").toVector,
      WordFrequency.wordFrequencyList(data),
      data
    )

    println(s"actual: $actual, expected: ${curr._2}")
  })
}

case class TrainInst(words: Vector[String], category: String)

object NaiveBayes {

  def classify(words: Vector[String],
               catsWithWordFreq: Map[String, WordFreq],
               data: => Vector[TrainInst]): Category = {

    val alpha = 0.3

    val sameWords: Iterable[WordFreq] =
      catsWithWordFreq.filterKeys(c => words.contains(c)).values
    val diffWords = catsWithWordFreq.filterKeys(c => !words.contains(c)).values

    val categories: Vector[String] = data.map(_.category).distinct

    val catAN = data.count(_.category == categories.head) + alpha * 2
    val catBN = data.count(_.category == categories.last) + alpha * 2

    def countWordsProb(words: Iterable[WordFreq],
                       cats: Vector[Category],
                       freqSum: (Double, Double, Double) => Double) = {

      def smoothing(myCat: Category, catN: Double): Double = {
        math.log(words.foldLeft(0.0)((prev, curr) => {
          val freq: Double = if (curr.isDefinedAt(myCat)) {
            freqSum(catN, curr(myCat), alpha)
          } else alpha
          prev + (freq / catN)
        }))
      }

      val firstCatFreqs  = smoothing(cats.head, catAN)
      val secondCatFreqs = smoothing(cats.last, catBN)
      Map(cats.head -> firstCatFreqs, cats.last -> secondCatFreqs)
    }

    val sameProbs: Map[String, Double] =
      countWordsProb(sameWords, categories, (_, freq, alpha) => freq + alpha)
    val diffProbs: Map[String, Double] = countWordsProb(
      diffWords,
      categories,
      (catN, freq, alpha) => math.abs(catN - freq) + alpha)

    val catA = sameProbs(categories.head) + diffProbs(categories.head)
    val catB = sameProbs(categories.last) + diffProbs(categories.last)

    val pA = (catAN - 2 * alpha) / data.size
    val pB = (catBN - 2 * alpha) / data.size

    val isCatA = catA - catB

    val lossIfGoodMarkedAsSpam = 1
    val lossIfSpamMarkedAsGood = 2
    val isCatB =
      math.log((lossIfSpamMarkedAsGood * pB) / (lossIfGoodMarkedAsSpam * pA))

    if (isCatA > isCatB) categories.head
    else categories.last
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
