import Dependencies._

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "com.example",
      scalaVersion := "2.11.8",
      version := "0.1.0-SNAPSHOT"
    )),
  name := "k-means-mllib",
  libraryDependencies ++= Seq(
    scalaTest          % Test,
    "org.apache.spark" %% "spark-core" % "1.6.2",
    "org.apache.spark" %% "spark-mllib" % "1.6.2"
  )
)
