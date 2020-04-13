package me.benetis

import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

/**
  * https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#Rules
  * Any live cell with fewer than two live neighbours dies, as if by underpopulation.
  * Any live cell with two or three live neighbours lives on to the next generation.
  * Any live cell with more than three live neighbours dies, as if by overpopulation.
  * Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
  */
object GameOfLifeRulesSpec extends DefaultRunnableSpec {
  def spec = suite("Game of life rules spec")(
    testM("cell should die if it has no neighbours") {
      /*
      +---------+
      |---------|
      |---------|
      |-X-------|
      +---------+
       */
      val state = Set(Point(1, 1))

      for {
        nextState: LifeState <- GameOfLifeRules.nextState(state)
      } yield assert(nextState)(equalTo(Set.empty[Point]))
    },
    testM("cell should die if it has one neighbour") {
      /*
      +---------+
      |---------|
      |---------|
      |-XY------|
      +---------+
       */
      val state = Set(Point(1, 1), Point(2, 1))

      for {
        nextState: LifeState <- GameOfLifeRules.nextState(state)
      } yield assert(nextState)(equalTo(Set.empty[Point]))
    },
    testM("cell with two neighbours should live on") {
      /*
      +---------+
      |---------|
      |---------|
      |-XY------|
      +-YN------+
       */
      val state = Set(Point(1, 1), Point(2, 1), Point(1, 0), Point(2, 0))

      for {
        nextState: LifeState <- GameOfLifeRules.nextState(state)
      } yield assert(nextState)(equalTo(state))
    },
    testM("cell with three neighbours should live on") {
      /*
      +---------+
      |---------|
      |---------|
      |YXY------|
      +-Y-------+
       */
      val state = Set(Point(1, 1), Point(2, 1), Point(1, 0), Point(0, 1))
      val expectedState = Set(
        Point(2, 0),
        Point(1, 0),
        Point(1, 1),
        Point(0, 0),
        Point(2, 1),
        Point(0, 1),
        Point(1, 2)
      )

      for {
        nextState: LifeState <- GameOfLifeRules.nextState(state)
      } yield assert(nextState)(equalTo(expectedState))
    },
    testM("cell with 4 neighbours should die") {
      /* Input
      +---------+
      |---------|
      |-Y-------|
      |YXY------|
      +-Y-------+
       */
      /* Expected
      +---------+
      |---------|
      |-Y-------|
      |Y-Y------|
      +-Y-------+
       */
      val state =
        Set(Point(1, 1), Point(2, 1), Point(1, 0), Point(0, 1), Point(1, 2))
      val expected = Set(
        Point(0, 2),
        Point(2, 0),
        Point(2, 2),
        Point(1, 0),
        Point(0, 0),
        Point(2, 1),
        Point(0, 1),
        Point(1, 2)
      )

      for {
        nextState: LifeState <- GameOfLifeRules.nextState(state)
      } yield assert(nextState)(equalTo(expected))
    },
    testM("dead cell with 3 live neighbours becomes a live cell") {
      /* Input
      +---------+
      |---------|
      |-Y-------|
      |Y-Y------|
      +---------+
       */
      /* Expected
      +---------+
      |---------|
      |-Y-------|
      |-X-------|
      +---------+
       */
      val state = Set(Point(0, 1), Point(1, 2), Point(2, 1))
      val expected = Set(Point(1, 2), Point(1, 1))

      for {
        nextState: LifeState <- GameOfLifeRules.nextState(state)
      } yield assert(nextState)(equalTo(expected))
    },
  )
}
