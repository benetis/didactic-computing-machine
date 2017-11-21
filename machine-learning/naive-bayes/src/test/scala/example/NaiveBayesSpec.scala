package example

import org.scalatest._

class NaiveBayesSpec extends FreeSpec with Matchers {

  val input = Vector("test", "important", "document")

  val inputSameInDifferentRows = input ++ Vector("important")

  val inputSameInOneRowAndDifferentRows = input ++ Vector("important",
    "important")

  val diffCategoriesInput = Vector(
    TrainInst(Vector("y", "x"), "2"),
    TrainInst(Vector("x"), "1"),
    TrainInst(Vector("x"), "2")
  )

  "word frequencies" - {

    "should output word frequencies with categories split" in {
      assert(
        WordFrequency.splitCategoriesWithFrequencies(diffCategoriesInput) ==
          Map(
            "1" -> Map("x" -> 1),
            "2" -> Map("y" -> 1, "x" -> 2)
          )
      )
    }

    "should split different categories into separate Maps" in {
      assert(
        WordFrequency.splitIntoCategoryMaps(diffCategoriesInput) ==
          Map(
            "1" -> Vector("x"),
            "2" -> Vector("y", "x", "x")
          ))
    }

    "should generate a list of unique words" in {
      assert(
        WordFrequency.wordFrequencyList(input) ==
          Map("test" -> 1, "important" -> 1, "document" -> 1)
      )
    }

    "should count increment number if word appears twice in different rows" in {
      assert(
        WordFrequency.wordFrequencyList(inputSameInDifferentRows) ==
          Map("test" -> 1, "important" -> 2, "document" -> 1)
      )
    }

    "should count increment number if word appears twice in same training" in {
      assert(
        WordFrequency.wordFrequencyList(inputSameInOneRowAndDifferentRows) ==
          Map("test" -> 1, "important" -> 3, "document" -> 1)
      )
    }
  }

  "classification" - {
//    "should return spam/not_spam for both features for sanity check training set" in {
//
//      val sanityTest = Vector(
//        TrainInst(Vector("x", "x"), "spam"),
//        TrainInst(Vector("y", "y"), "not_spam"),
//        TrainInst(Vector("x", "x"), "spam"),
//        TrainInst(Vector("y", "y"), "not_spam"),
//        TrainInst(Vector("x", "x"), "spam"),
//        TrainInst(Vector("y", "y"), "not_spam"),
//        TrainInst(Vector("x", "x"), "spam"),
//        TrainInst(Vector("x", "x"), "spam"),
//        TrainInst(Vector("x", "x"), "spam"),
//        TrainInst(Vector("x", "x"), "spam"),
//        TrainInst(Vector("x", "x"), "spam"),
//        TrainInst(Vector("x", "x"), "spam"),
//        TrainInst(Vector("y", "y"), "not_spam"),
//        TrainInst(Vector("y", "y"), "not_spam"),
//        TrainInst(Vector("y", "y"), "not_spam"),
//        TrainInst(Vector("y", "y"), "not_spam"),
//        TrainInst(Vector("y", "y"), "not_spam")
//      )
//
//      assert(
//        NaiveBayes.classify(
//          Vector("x", "x"),
//          WordFrequency.splitCategoriesWithFrequencies(sanityTest),
//          sanityTest
//        ) == "spam"
//      )
//
//      assert(
//        NaiveBayes.classify(
//          Vector("y", "y"),
//          WordFrequency.splitCategoriesWithFrequencies(sanityTest),
//          sanityTest
//        ) == "not_spam"
//      )
//    }

    "should return that its spam given multiple different words" in {

      val trainingSet = Vector(
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("x", "x", "x"), "spam"),
        TrainInst(Vector("x", "x", "x"), "spam"),
        TrainInst(Vector("x", "x", "x"), "spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("w", "y", "w"), "not_spam"),
        TrainInst(Vector("x", "y", "x"), "spam"),
        TrainInst(Vector("x", "y", "x"), "spam"),
        TrainInst(Vector("x", "y", "x"), "spam"),
        TrainInst(Vector("x", "y", "x"), "spam")

      )

      assert(
        NaiveBayes.classify(
          Vector("w"),
          WordFrequency.splitCategoriesWithFrequencies(trainingSet),
          trainingSet
        ) == "not_spam"
      )

      assert(
        NaiveBayes.classify(
          Vector("x"),
          WordFrequency.splitCategoriesWithFrequencies(trainingSet),
          trainingSet
        ) == "spam"
      )
    }
  }

}
