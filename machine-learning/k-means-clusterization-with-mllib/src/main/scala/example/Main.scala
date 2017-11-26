package example

import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

object Main extends App {

  val conf = new SparkConf()
    .setAppName("K means cluster")
    .setMaster("local")

  val sc = SparkContext
    .getOrCreate(conf)

  val data = sc.parallelize(
    Vector(
      Vector(-4.0, -1.0, -4.0),
      Vector(2.0, 0.0, 0.0),
      Vector(1.0, -2.0, 4.0),
      Vector(-3.0, -4.0, -1.0),
      Vector(2.0, -4.0, 0.0),
      Vector(2.0, 1.0, -5),
      Vector(3.0, -3.0, 0.0),
      Vector(-1.0, -1.0, 1.0)
    ).map(t => Vectors.dense(t.toArray)))

  val numOfClusters   = 3
  val numOfIterations = 100

  val clusters = KMeans.train(data, numOfClusters, numOfIterations)

  println("Cluster centers")
  clusters.clusterCenters.foreach(println)

  println("Squared Errors")
  println(clusters.computeCost(data))

  println("Predictions")
  println(clusters.predict(Vectors.dense(0.0, 0.0, 0.0)))
  println(clusters.predict(Vectors.dense(-3.0, -2.0, 1.5)))
}
