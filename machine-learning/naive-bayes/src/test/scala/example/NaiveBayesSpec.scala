package example

import org.scalatest._

class NaiveBayesSpec extends FreeSpec with Matchers {

  val input = Vector(
    TrainingInstance(Vector("test"), "spam"),
    TrainingInstance(Vector("important", "document"), "not_spam")
  )

  val inputSameInDifferentRows = input ++ Vector(
    TrainingInstance(Vector("important"), "not_spam")
  )

  val inputSameInOneRowAndDifferentRows = input ++ Vector(
    TrainingInstance(Vector("important", "important"), "not_spam")
  )


  "word frequencies" - {
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
