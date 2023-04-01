package flashcards

import java.time.Instant

object Flashcard {

  case class Id(id: Long)
  case class Question(getValue: String)
  case class Answer(getvalue: String)
  case class DueOn(getValue: Instant) {
    def isDue: Boolean = getValue.isBefore(Instant.now)
    def nextDueOn: NextDueOn = { /* formulas to adjust later */
      val now = Instant.now
      val hard = now.plusSeconds(60)
      val medium = now.plusSeconds(60 * 60)
      val easy = now.plusSeconds(60 * 60 * 24)
      NextDueOn(DueOn(hard), DueOn(medium), DueOn(easy))
    }
  }

  case class Entity(id: Id, question: Question, answer: Answer, due: DueOn)

  case class NextDueOn(hard: DueOn, good: DueOn, easy: DueOn)

}
