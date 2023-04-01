package cli_render

enum Element {

  case Text(text: String)
  case Div(children: Element*)
}
