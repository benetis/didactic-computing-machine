import Dependencies._

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val javaFXModules =
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")

// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _                            => throw new Exception("Unknown platform!")
}

lazy val root = (project in file("."))
  .settings(
    name := "game-of-life",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "dev.zio" %% "zio" % "1.0.0-RC18-2",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-test" % "1.0.0-RC18-2" % "test",
      "dev.zio" %% "zio-test-sbt" % "1.0.0-RC18-2" % "test",
      "dev.zio" %% "zio-test-magnolia" % "1.0.0-RC18-2" % "test" // optional
    ),
    libraryDependencies += "org.scalafx" % "scalafx_2.13" % "12.0.2-R18",
    libraryDependencies ++= javaFXModules.map(
      m => "org.openjfx" % s"javafx-$m" % "12.0.2" classifier osName
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
