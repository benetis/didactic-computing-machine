ThisBuild / scalaVersion := "3.3.6"

lazy val flinkVersion = "2.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "fraud-alerts",
    organization := "flinkTry",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "org.apache.flink" % "flink-streaming-java" % flinkVersion % Provided,
      "org.apache.flink" % "flink-clients" % flinkVersion % Provided
    ),
    // fat jar
    assembly / mainClass := Some("flinkTry.CustomJob"),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case _                        => MergeStrategy.first
    }
  )
