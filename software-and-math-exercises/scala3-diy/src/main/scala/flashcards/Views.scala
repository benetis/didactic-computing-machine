package flashcards

import cli_render.Element
import flashcards.Flashcard.*

object Views {

  def renderCard(flashcard: Flashcard.Entity) =
    Element.Div(
      Element.Div(
        Element.Text(flashcard.question.getValue)
      ),
      Element.Div(
        Element.Text(flashcard.answer.getvalue)
      ),
      difficultyButtons(flashcard.due.nextDueOn)
    )

  def difficultyButtons(nextDueOn: NextDueOn) =
    Element.Div(
      Element.Text(s"Hard (${nextDueOn.hard.getValue})"),
      Element.Text(s"Good (${nextDueOn.good.getValue})"),
      Element.Text(s"Easy (${nextDueOn.easy.getValue})")
    )

}
