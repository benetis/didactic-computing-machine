import Dependencies._

ThisBuild / scalaVersion := "2.13.7"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

val circeVersion = "0.14.1"

lazy val root = (project in file("."))
  .settings(
    name := "shopping_cart",
    scalacOptions += "-Ymacro-annotations",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "tf.tofu" %% "derevo-circe" % "0.13.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.typelevel" %% "cats-core" % "2.3.0",
      "org.typelevel" %% "cats-effect" % "3.3.5",
      "dev.optics" %% "monocle-core" % "3.1.0",
      "dev.optics" %% "monocle-macro" % "3.1.0"
    )
  )
