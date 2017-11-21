package example
import example.WordFrequency.{Category, WordFreq}
import scalaz.Scalaz._

object SpamFilter extends App with DataParser {
  val data = Loader.loadData()

  val catsWithFreqs = WordFrequency.splitCategoriesWithFrequencies(data)
  println(catsWithFreqs)
}

case class TrainInst(words: Vector[String], category: String)

object NaiveBayes {

  def classify(words: Vector[String],
               catsWithWordFreq: Map[Category, WordFreq],
               data: => Vector[TrainInst]): Category = {
    val catProbs: Vector[(Category, Double, Int)] = catsWithWordFreq.map {
      case ((category, wordFreq)) =>
        val catN =
          data.count(_.category == category)

        val sameWords: WordFreq = wordFreq.filterKeys(c => words.contains(c))
        val diffWords           = wordFreq.filterKeys(c => !words.contains(c))

        val sameWordsProb = sameWords.values
        val diffWordsProb = diffWords.values.map(x => math.abs(x - catN))

        (category, (sameWordsProb ++ diffWordsProb).product / math.pow(catN, data.size), catN)

    }.toVector

    val catA = catProbs.head
    val catB = catProbs.last

    val pA = catA._3.toDouble / data.size
    val pB = catB._3.toDouble / data.size


    val isCatA = catA._2 / catB._2

    val isCatB = ((10 - 0) * pB) / ((1 - 0) * pA)

    if(isCatA > isCatB) catA._1
    else catB._1
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
