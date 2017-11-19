import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "org.scalanlp" %% "breeze" % "0.13.2",
      "org.scalanlp" %% "breeze-viz" % "0.13.2",
      "org.scalaz" %% "scalaz-core" % "7.2.16"
    )
  )
