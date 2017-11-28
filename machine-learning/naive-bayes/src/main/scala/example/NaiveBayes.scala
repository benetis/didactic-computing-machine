package example
import example.WordFrequency.{Category, WordFreq}
import scalaz.Scalaz._
import scalaz.Semigroup

object SpamFilter extends App with DataParser {
  val data = Loader.loadData()

  val test = Loader.loadTest()

//  val testSentences: Vector[(String, String)] = Vector(
//    ("rate rate rate", "spam"),
//    ("i am going to sleep", "ham"),
//    ("urgent discount", "spam"),
//    ("i will call you later", "ham"),
//    ("call our customer service", "spam"),
//    ("free monthly ringtone", "spam"),
//    ("call freephone", "spam"),
//    ("wish you guys great semester", "ham"),
//    ("win free vip ticket", "spam")
//  )

  var accurate       = 0
  var countedAlready = 0

  test.foreach((curr: TrainInst) => {
    val actual = NaiveBayes.classify(
      curr.words,
      WordFrequency.wordFrequencyList(data),
      data
    )

    if (curr.category == actual) {
      accurate += 1
      countedAlready += 1
    } else {
      countedAlready += 1
    }

    println(accurate.toDouble / countedAlready)

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
    val lossIfSpamMarkedAsGood = 1
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

  def loadTest(): Vector[TrainInst] = {
    val bufferedSource = io.Source.fromResource("./test.csv")
    val data           = bufferedSource.getLines.map(trimLine).toVector
    bufferedSource.close
    data
  }
}

trait DataParser {
  val spam    = "spam"
  val notSpam = "ham"

  val cleanSet = ",.!?:\"#+".toSet

  val wordsToIgnore = Vector(
    "a",
    "about",
    "above",
    "after",
    "again",
    "against",
    "all",
    "am",
    "an",
    "and",
    "any",
    "are",
    "aren't",
    "as",
    "at",
    "be",
    "because",
    "been",
    "before",
    "being",
    "below",
    "between",
    "both",
    "but",
    "by",
    "can't",
    "cannot",
    "could",
    "couldn't",
    "did",
    "didn't",
    "do",
    "does",
    "doesn't",
    "doing",
    "don't",
    "down",
    "during",
    "each",
    "few",
    "for",
    "from",
    "further",
    "had",
    "hadn't",
    "has",
    "hasn't",
    "have",
    "haven't",
    "having",
    "he",
    "he'd",
    "he'll",
    "he's",
    "her",
    "here",
    "here's",
    "hers",
    "herself",
    "him",
    "himself",
    "his",
    "how",
    "how's",
    "i",
    "i'd",
    "i'll",
    "i'm",
    "i've",
    "if",
    "in",
    "into",
    "is",
    "isn't",
    "it",
    "it's",
    "its",
    "itself",
    "let's",
    "me",
    "more",
    "most",
    "mustn't",
    "my",
    "myself",
    "no",
    "nor",
    "not",
    "of",
    "off",
    "on",
    "once",
    "only",
    "or",
    "other",
    "ought",
    "our",
    "ours",
    "ourselves",
    "out",
    "over",
    "own",
    "same",
    "shan't",
    "she",
    "she'd",
    "she'll",
    "she's",
    "should",
    "shouldn't",
    "so",
    "some",
    "such",
    "than",
    "that",
    "that's",
    "the",
    "their",
    "theirs",
    "them",
    "themselves",
    "then",
    "there",
    "there's",
    "these",
    "they",
    "they'd",
    "they'll",
    "they're",
    "they've",
    "this",
    "those",
    "through",
    "to",
    "too",
    "under",
    "until",
    "up",
    "very",
    "was",
    "wasn't",
    "we",
    "we'd",
    "we'll",
    "we're",
    "we've",
    "were",
    "weren't",
    "what",
    "what's",
    "when",
    "when's",
    "where",
    "where's",
    "which",
    "while",
    "who",
    "who's",
    "whom",
    "why",
    "why's",
    "with",
    "won't",
    "would",
    "wouldn't",
    "you",
    "you'd",
    "you'll",
    "you're",
    "you've",
    "your",
    "yours",
    "yourself",
    "yourselves"
  ).toSet

  def trimLine(line: String): TrainInst = {

    def buildInstance(cat: String): TrainInst = {
      TrainInst(line
                  .drop(cat.length + 1)
                  .trim
                  .split(" ")
                  .map(_.filterNot(cleanSet))
                  .filterNot(wordsToIgnore)
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
