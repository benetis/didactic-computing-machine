import Dependencies._

ThisBuild / scalaVersion     := "2.13.7"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.benetis"
ThisBuild / organizationName := "benetis"

lazy val root = (project in file("."))
  /* Breaking purity with one dependency for sbt test interface */
  .settings(libraryDependencies ++= Seq("org.scala-sbt" % "test-interface" % "1.0"))
  .settings(
    name := "scala-amish",
    // Test / testOptions := Seq(Tests.Filter(name => name endsWith "Test")),
    testFrameworks += new TestFramework("me.benetis.testix.sbt.TestixFramework")
  )
