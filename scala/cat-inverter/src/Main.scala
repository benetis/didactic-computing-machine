/*
Conversions done using algo from:
https://gist.github.com/mjackson/5311256
 */

/* In the set [0, 255] */
case class RGB(R: Int, G: Int, B: Int) {
  def toHSL: HSL = {
    val r = R / 255d
    val g = G / 255d
    val b = B / 255d

    val max = Vector(r, g, b).max
    val min = Vector(r, g, b).min

    val lightness = (max + min) / 2d

    val d = max - min

    val saturation = if (max == min) 0d else if (lightness > 0.5) {
      d / (2 - max - min)
    } else {
      d / (max + min)
    }

    val hue = if (max == min) {
      0d
    } else {
      max match {
        case x if x == r =>
          val delt = if (g < b) 6d else 0d
          (g - b) / d + delt
        case x if x == g => (b - r) / d + 2
        case _ => (r - g) / d + 4
      }
    } / 6

    HSL(hue, saturation, lightness)
  }

}

/* in the set [0, 1] */
case class HSL(h: Double, s: Double, l: Double) {
  implicit def toInt(double: Double): Int = double.round.toInt

  def toRGB: RGB = {
    if (s == 0) RGB(l * 255, l * 255, l * 255)
    else {
      def hue2RGB(p: Double, q: Double, temp: Double): Double = {

        val temp2: Double = temp match {
          case x if x < 0 => x + 1
          case x if x > 1 => x - 1
          case x => x
        }

        temp2 match {
          case x if x < 1 / 6d => p + (q - p) * 6d * x
          case x if x < 1 / 2d => q
          case x if x < 2 / 3d => p + (q - p) * (2 / 3d - x) * 6d
          case _ => p
        }
      }

      val q = if (l < 0.5) l * (1 + s) else l + s - l * s
      val p = 2 * l - q
      val r = hue2RGB(p, q, h + 1 / 3d)
      val g = hue2RGB(p, q, h)
      val b = hue2RGB(p, q, h - 1 / 3d)

      RGB(r * 255, g * 255, b * 255)
    }
  }
}

object FileManager {

  import scala.io.Source
  import java.io._

  def read(fileName: String): Iterator[String] = {
    Source.fromFile(fileName).getLines()
  }

  def write(content: => String, fileName: String = "../inverted-cat.ppm") = {
    val file = new File(fileName)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(content)
    bw.close()
  }
}

object ColorInverter {
  def invertHue(hsl: HSL): HSL = {
    HSL(1 - hsl.h, hsl.s, hsl.l)
  }

  def transformAndInvert(grouped: Seq[String]): String = {
    val rgb = RGB(
      grouped(0).toInt,
      grouped(1).toInt,
      grouped(2).toInt
    )
    val invertedHSL = invertHue(rgb.toHSL)
    val invertedRGB = invertedHSL.toRGB
    List(invertedRGB.R, invertedRGB.G, invertedRGB.B).mkString("\n")
  }
}

object Main extends App {

  FileManager
    .write(
      {

        val fileStream = FileManager.read(args(0)).grouped(3)

        val meta = fileStream.next().mkString("\n")

        meta ++ "\n" ++
          fileStream
            .filter(rgb => rgb.length == 3)
            .map(ColorInverter.transformAndInvert)
            .mkString("\n")
      }
    )
}