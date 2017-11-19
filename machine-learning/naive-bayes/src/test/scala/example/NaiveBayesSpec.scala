package example

import org.scalatest._

class NaiveBayesSpec extends FreeSpec with Matchers {

  val input = Vector(
    TrainInst(Vector("test"), "spam"),
    TrainInst(Vector("important", "document"), "not_spam")
  )

  val inputSameInDifferentRows = input ++ Vector(
    TrainInst(Vector("important"), "not_spam")
  )

  val inputSameInOneRowAndDifferentRows = input ++ Vector(
    TrainInst(Vector("important", "important"), "not_spam")
  )

  val diffCategoriesInput = Vector(
    TrainInst(Vector("y", "x"), "2"),
    TrainInst(Vector("x"), "1"),
    TrainInst(Vector("x"), "2"),
    TrainInst(Vector("x"), "3")
  )


  "word frequencies" - {

    "should split different categories into separate Maps" in {
      assert(
        WordFrequency.splitIntoCategoryMaps(diffCategoriesInput) ==
        Map(
          "1" -> Vector("x"),
          "2" -> Vector("y", "x", "x"),
          "3" -> Vector("x")
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

}
