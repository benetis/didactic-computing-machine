import Dependencies._

ThisBuild / scalaVersion     := "2.13.7"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.benetis"
ThisBuild / organizationName := "benetis"

lazy val root = (project in file("."))
  .settings(
    name := "scala-amish",
    libraryDependencies += scalaTest % Test
  )
