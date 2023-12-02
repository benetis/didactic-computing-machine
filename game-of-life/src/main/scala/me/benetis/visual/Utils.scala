package me.benetis.visual

import me.benetis.{LifeState, Point}

object Utils {

//  def debug(state: LifeState): Unit = {
//
//    if (state.isEmpty)
//      println(s"Given state is empty: ${state.toString()}")
//    else {
//
//      val minX = state.minBy(_.x).x
//      val maxX = state.maxBy(_.x).x
//
//      val minY = state.minBy(_.y).y
//      val maxY = state.maxBy(_.y).y
//
//      val rows = Range.inclusive(minY, maxY)
//      val columns = Range.inclusive(minX, maxX)
//
//      val emptyMatrix: Seq[IndexedSeq[Point]] =
//        rows.map(r => columns.map(c => Point(c, r)))
//
//      def printRow(row: Seq[Point], rowNumber: Int): String = {
//        row.zipWithIndex
//          .map {
//            case (p, columnNumber) =>
//              val rowNumberStr = {
//                if (columnNumber == 0)
//                  s"|${rowNumber}| "
//                else
//                  ""
//              }
//
//              val cellValue = {
//                if (state.contains(p))
//                  s"X"
//                else
//                  s"-"
//              }
//
//              s"$rowNumberStr$cellValue"
//          }
//          .mkString(" ")
//      }
//
//      def bottomRow(): String = {
//        val first = "+-+--" ++ List.fill(columns.size)("--").mkString
//        val firstWithPlus = first.updated(first.length - 1, '+')
//        val second = "| | " ++ columns.map(i => s"$i").mkString(" ") ++ " |"
//        firstWithPlus ++ "\r\n" ++ second ++ "\r\n" ++ first
//      }
//
//      def topRow(): String = {
//        val top = "+-+--" ++ List.fill(columns.size)("--").mkString
//        top.updated(top.length - 1, '+')
//      }
//
//      val printCellsWithoutDomain =
//        emptyMatrix.reverse.zipWithIndex
//          .map {
//            case (row, i) =>
//              printRow(row, Math.abs(i - emptyMatrix.size)) ++ " |"
//          }
//
//      val printCells =
//        topRow() ++ "\r\n" ++ printCellsWithoutDomain.mkString("\r\n") ++ "\r\n" ++ bottomRow()
//
//      println(printCells)
//    }
//  }

}
