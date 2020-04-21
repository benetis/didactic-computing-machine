package me.benetis

import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, document, html}
import org.scalajs.dom.raw.{Element, WebGLRenderingContext}
import me.benetis.opengl.OpenGLExts._
import dom._
import zio.Task

object Render {

  val program: Task[Unit] = Task {
    val canvas: html.Canvas =
      document.querySelector("#glCanvas").asInstanceOf[html.Canvas]

    val gl: WebGLRenderingContext = canvas
      .getContext("webgl")
      .asInstanceOf[WebGLRenderingContext]

    if (gl == null) {
      dom.window.alert("Webgl is not working")
    }

    gl.clearColor(0.0, 0.0, 0.0, 1.0)
    gl.clear(WebGLRenderingContext.COLOR_BUFFER_BIT)
  }

}
