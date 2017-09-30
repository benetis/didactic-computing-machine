import ID3.{entropy, splitByParam}

object ID3 extends App {

  case class Node(value: Option[String])

  type TrainingInstance = Vector[String]

  type Param = String

  val names = Vector("x1", "x2", "x3", "x4", "Class")

  val inputValues = Vector(
    //x1   x2   x3   x4   Cl
    Vector("c", "b", "b", "c", "Y"),
    Vector("d", "c", "b", "c", "G"),
    Vector("c", "c", "d", "d", "R"),
    Vector("b", "d", "c", "b", "R"),
    Vector("d", "c", "d", "c", "G"),
    Vector("d", "c", "c", "b", "G"),
    Vector("d", "b", "c", "c", "Y"),
    Vector("c", "d", "d", "d", "Y"),
    Vector("b", "b", "b", "b", "R"),
    Vector("b", "c", "c", "b", "G")
  )

  def learning() = {
    val rootNode = Node(None)
    divide_and_conquer(inputValues, rootNode)
  }

  def divide_and_conquer(
      subset: Vector[TrainingInstance],
      node: Node
  ) = {

    def hasOnlyOneClass(subset: Vector[TrainingInstance]): Boolean = {
      val amountOfClasses = subset.map(_.last).size
      val amountOfUniqueClasses = subset.map(_.last).toSet.size

      amountOfClasses == amountOfUniqueClasses
    }

    if (hasOnlyOneClass(subset)) {
      subset.head.last //Return Class
    } else {
//      //Check which parameter has best entropy


      val entropyForEachParam: Vector[(Param, Double)] = {
        names.dropRight(1).zipWithIndex.map { case (paramName, i) => //-1 for class
          paramName -> splitByParam(subset, i).map {
            case (param, set: Vector[TrainingInstance]) =>
              entropy(set)
          }.sum
        }
      }

      val x = 1

    }

  }

  def splitByParam(subset: Vector[TrainingInstance], nthParam: Int): Map[Param, Vector[TrainingInstance]] =
    subset.groupBy(identity((x: TrainingInstance) => x(nthParam)))


  def entropy(subset: Vector[TrainingInstance]): Double = {

    val log2 = (x: Double) => scala.math.log10(x) / scala.math.log10(2.0)

    val classesCount = subset.map(_.last).groupBy(identity)

    val thisSetSize: Double = classesCount.map(_._2.size).sum

    val entropy = classesCount.foldLeft(0.0d) {
      case (res: Double, (_, iSet: Vector[String])) =>
        res + log2(iSet.size / thisSetSize) * (iSet.size / thisSetSize)
    } * -1

    entropy
  }

}
