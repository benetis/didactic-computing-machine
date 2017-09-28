object ID3 extends App {

  case class Node(value: Option[String])

  type TrainingInstance = Vector[String]

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
    }
  }

  /**
    * Split into subsets for specific param
    * Find how many different classes in subset
    */
  def informationGain(subset: Vector[TrainingInstance],
                      nthParam: Int): Double = {

    type Param = String
    val splitByParam: Map[Param, Vector[TrainingInstance]] =
      subset.groupBy(identity((x: TrainingInstance) => x(nthParam)))

    val log2 = (x: Double) => scala.math.log10(x) / scala.math.log10(2.0)

    val setEntropy = splitByParam.map {
      case (param, set: Vector[TrainingInstance]) =>
        val classesCount = set.map(_.last).groupBy(identity)

        val wholeSet: Double = subset.size

        val thisSetSize: Double = classesCount.map(_._2.size).sum

        val probabilityThisSetTaken = thisSetSize / wholeSet

        val entropy = classesCount.foldLeft(0.0d) {
          case (res: Double, (_, iSet: Vector[String])) =>
            res + log2(iSet.size / thisSetSize) * (iSet.size / thisSetSize)
        } * -1

        probabilityThisSetTaken * entropy
    }

    setEntropy.sum
  }

}
