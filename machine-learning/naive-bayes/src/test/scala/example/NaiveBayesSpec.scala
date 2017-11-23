package example

import org.scalatest._

class NaiveBayesSpec extends FreeSpec with Matchers {

  val input = Vector(
    TrainInst(Vector("test", "important", "document"), "1")
  )

  val inputSameInDifferentRows = input ++ Vector("important")

  val inputSameInOneRowAndDifferentRows = input ++ Vector("important",
                                                          "important")

  val diffCategoriesInput = Vector(
    TrainInst(Vector("y", "x"), "2"),
    TrainInst(Vector("x"), "1"),
    TrainInst(Vector("x"), "2")
  )

  "word frequencies" - {

    "should make all words lowercase and group as one" in {

      val input = Vector(
        TrainInst(Vector("Upper", "UPPER", "upper"), "1")
      )
      assert(
        WordFrequency.wordFrequencyList(input) ==
          Map("upper" -> Map("1" -> 3))
      )
    }

    "should output word frequencies with categories split" in {
      assert(
        WordFrequency.wordFrequencyList(diffCategoriesInput) ==
          Map(
            "x" -> Map("1" -> 1, "2" -> 2),
            "y" -> Map("2" -> 1)
          )
      )
    }

    "should generate a list of unique words" in {
      assert(
        WordFrequency.wordFrequencyList(input) ==
          Map("test"      -> Map("1" -> 1),
              "important" -> Map("1" -> 1),
              "document"  -> Map("1" -> 1))
      )
    }
  }

  "classification" - {
    "should return spam/not_spam for both features for sanity check training set" in {

      val sanityTest = Vector(
        TrainInst(Vector("x"), "spam"),
        TrainInst(Vector("y"), "not_spam"),
        TrainInst(Vector("x"), "spam"),
        TrainInst(Vector("y"), "not_spam"),
        TrainInst(Vector("x"), "spam"),
        TrainInst(Vector("y"), "not_spam"),
        TrainInst(Vector("x"), "spam"),
        TrainInst(Vector("x"), "spam"),
        TrainInst(Vector("x"), "spam"),
        TrainInst(Vector("x"), "spam"),
        TrainInst(Vector("x"), "spam"),
        TrainInst(Vector("x"), "spam"),
        TrainInst(Vector("y"), "not_spam"),
        TrainInst(Vector("y"), "not_spam"),
        TrainInst(Vector("y"), "not_spam"),
        TrainInst(Vector("y"), "not_spam"),
        TrainInst(Vector("y"), "not_spam")
      )

      assert(
        NaiveBayes.classify(
          Vector("x", "x"),
          WordFrequency.wordFrequencyList(sanityTest),
          sanityTest
        ) == "spam"
      )

//      assert(
//        NaiveBayes.classify(
//          Vector("y", "y"),
//          WordFrequency.splitCategoriesWithFrequencies(sanityTest),
//          sanityTest
//        ) == "not_spam"
//      )
    }

    "should return that its spam given multiple different words" in {

      val trainingSet = Vector(
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("x", "x", "w"), "spam"),
        TrainInst(Vector("x", "x", "x"), "spam"),
        TrainInst(Vector("x", "x", "x"), "spam"),
        TrainInst(Vector("x", "x", "x"), "spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("w", "x", "w"), "not_spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("x", "y", "x"), "spam"),
        TrainInst(Vector("x", "y", "x"), "spam"),
        TrainInst(Vector("x", "y", "x"), "spam"),
        TrainInst(Vector("x", "y", "x"), "spam")
      )

//      assert(
//        NaiveBayes.classify(
//          Vector("w"),
//          WordFrequency.splitCategoriesWithFrequencies(trainingSet),
//          trainingSet
//        ) == "not_spam"
//      )
//
//      assert(
//        NaiveBayes.classify(
//          Vector("x"),
//          WordFrequency.splitCategoriesWithFrequencies(trainingSet),
//          trainingSet
//        ) == "spam"
//      )
    }

    "should classify 'real world spam' as spam" in {

      def splitW(s: String) = s.split(" ").toVector

      val input = Vector(
        TrainInst(
          splitW(
            "thanks for your ringtone order, reference number x49. your mobile will be charged 4.50. should your tone not arrive please call customer services 09065989182"
          ),
          "spam"
        ),
        TrainInst(
          splitW(
            "dear voucher holder 2 claim your 1st class airport lounge passes when using your holiday voucher call 08704439680. when booking quote 1st class x 2"),
          "spam"
        ),
        TrainInst(
          splitW(
            "ok... but bag again.."
          ),
          "not_spam"
        ),
        TrainInst(
          splitW(
            "let me know if you need anything else. salad or desert or something... how many beers shall i get?"
          ),
          "not_spam"
        )
      )

//      assert(
//        NaiveBayes.classify(
//          Vector("order"),
//          WordFrequency.splitCategoriesWithFrequencies(input),
//          input
//        ) == "spam"
//      )
    }
  }

}
