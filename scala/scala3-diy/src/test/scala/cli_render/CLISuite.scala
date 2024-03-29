package cli_render

import cli_render.Render.ConsoleOutput

import scala.util.*

class CLISuite extends munit.FunSuite {
  test("Compile should render a simple string") {

    val text = Element.Text("Hello World")

    assertEquals(Render.compile(text), "Hello World".output)
  }

  test("Compile should render empty Div") {

    val div = Element.Div()

    assertEquals(Render.compile(div), "\n".output)
  }

  test("Compile should render empty Span") {

    val span = Element.Span()

    assertEquals(Render.compile(span), "".output)
  }

  test("Compile should render a Div with a single child") {

    val div = Element.Div(
      Element.Text("Hello World")
    )

    assertEquals(Render.compile(div), "\nHello World".output)
  }

  test("Compile should render a Span with multiple children") {

    val span = Element.Span(
      Element.Text("Hello"),
      Element.Text(" "),
      Element.Text("World")
    )

    assertEquals(Render.compile(span), "Hello World".output)
  }

  test("Each div should be in a new line") {

    val div = Element.Span(
      Element.Div(
        Element.Text("Hello"),
        Element.Text(" "),
        Element.Text("World")
      ),
      Element.Div(
        Element.Text("Hello"),
        Element.Text(" "),
        Element.Text("World")
      )
    )

    assertEquals(Render.compile(div), "\nHello World\nHello World".output)
  }

  test("Should render simple UI with question, answer and choices") {

    val ui = Element.Span(
      Element.Div(
        Element.Text("What is your name?")
      ),
      Element.Div(
        Element.Text("John")
      ),
      Element.Div(
        Element.Text("Remind me in:")
      ),
      Element.Div(
        Element.Text(">In 1<"),
        Element.Text(" "),
        Element.Text(">In 2<")
      )
    )

    assertEquals(
      Render.compile(ui),
      """
        |What is your name?
        |John
        |Remind me in:
        |>In 1< >In 2<""".stripMargin.output
    )
  }

  extension (s: String) def output: ConsoleOutput = ConsoleOutput(s)

}
