
object ID3 extends App {

  case class Node(value: Option[String])

  type TrainingInstance = Vector[String]

  val names = Vector("x1", "x2", "x3", "Class")

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

    def hasOnlyOneClass(set: Vector[TrainingInstance]): Boolean = {
      val amountOfClasses = set.map(_.last).size
      val amountOfUniqueClasses = set.map(_.last).toSet.size

      amountOfClasses == amountOfUniqueClasses
    }

    if(hasOnlyOneClass(subset)) {
      subset.head.last //Return Class
    }
  }
}
