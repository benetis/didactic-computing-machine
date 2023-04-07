package cli_render

import effects._

object Render {

  case class ConsoleOutput(value: String)

  def render(element: Element): IO[Unit] = {
    for {
      _ <- clearScreen
      _ <- IO.effect {
        println(compile(element).value)
      }
    } yield ()

  }

  def compile(element: Element): ConsoleOutput = {

    element match
      case Element.Text(text) => ConsoleOutput(text)

      case Element.Div(children @ _*) =>
        val childrenOutput = children.map(compile)
        val childrenText = childrenOutput.map(_.value).mkString
        ConsoleOutput("\n" + childrenText)
      case Element.Span(children @ _*) =>
        val childrenOutput = children.map(compile)
        val childrenText = childrenOutput.map(_.value).mkString
        ConsoleOutput(childrenText)
  }

  private def clearScreen: IO[Unit] = {
    IO.effect {
      print("\u001b[2J")
    }
  }

}
