import Dependencies._

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "me.benetis"
ThisBuild / organizationName := "benetis"

lazy val root = (project in file("."))
  .settings(
    name := "game-of-life",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "dev.zio" %%% "zio" % "1.0.0-RC18-2",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-test" % "1.0.0-RC18-2" % "test",
      "dev.zio" %% "zio-test-sbt" % "1.0.0-RC18-2" % "test",
      "dev.zio" %% "zio-test-magnolia" % "1.0.0-RC18-2" % "test" // optional
    ),
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-RC5",
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .enablePlugins(ScalaJSPlugin)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
