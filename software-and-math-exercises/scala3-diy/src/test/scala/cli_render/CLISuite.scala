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

    assertEquals(Render.compile(div), "".output)
  }

  test("Compile should render a Div with a single child") {

    val div = Element.Div(
      Element.Text("Hello World")
    )

    assertEquals(Render.compile(div), "Hello World".output)
  }

  test("Compile should render a Div with multiple children") {

    val div = Element.Div(
      Element.Text("Hello"),
      Element.Text(" "),
      Element.Text("World")
    )

    assertEquals(Render.compile(div), "Hello World".output)
  }

  extension (s: String) def output: ConsoleOutput = ConsoleOutput(s)

}
