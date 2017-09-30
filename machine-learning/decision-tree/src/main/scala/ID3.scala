import ID3.{entropy, splitByParam}

object ID3 extends App {

  type TrainingInstance = Vector[String]

  sealed trait Tree {
    def traverse(tree: Tree)(f: Tree => Unit): Unit = {
      tree match {
        case (node: Node) =>
          f(node)
          node.children.foreach(traverse(_)(f))
        case (leaf: Leaf) => f(leaf)
      }
    }

    val value: Option[String]

    val feature: Option[String]
  }

  case class Node(value: Option[String], feature: Option[String], children: Seq[Tree])(
      implicit dummy: Manifest[Node])
      extends Tree

  object Node {
    def apply(value: Option[String], feature: Option[String])(children: Tree*) =
      new Node(value, feature, children)
  }

  case class Leaf(value: Option[String], feature: Option[String]) extends Tree

  case class Param(id: Int, name: String)

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

  val testValues = Vector(
    Vector("b", "b", "c", "d", "Y"),
    Vector("b", "d", "b", "b", "R")
  )

  val paramNames = names.zipWithIndex.map { case (n, i) => Param(i, n) }

  def learning() = {
    val rootNode = Node(None, None)()


    divide_and_conquer(paramNames, inputValues, rootNode)
  }

  def divide_and_conquer(
      names: Vector[Param],
      subset: Vector[TrainingInstance],
      node: Tree,
      param: Option[Param] = None
  ): Tree = {

    def hasOnlyOneClass(subset: Vector[TrainingInstance]): Boolean =
      subset.map(_.last).toSet.size == 1

    def bestParameterToDivide(): (Param, Double) = {
      val entropyForEachParam: Vector[(Param, Double)] = {
        names.dropRight(1).map { p => //dropRight to exclude class
          p -> splitByParam(subset, p).map {
            case (param, set: Vector[TrainingInstance]) =>
              val probabilityThisSetIsTaken
                : Double = set.size.toDouble / subset.size
              entropy(set) * probabilityThisSetIsTaken
          }.sum
        }
      }

      val lowestEntropy: (Param, Double) = entropyForEachParam.minBy(_._2)

      println(s"Picking $lowestEntropy")

      lowestEntropy

    }

    if (hasOnlyOneClass(subset)) {
      Leaf(Some(subset.head.last), Some(subset.head(names.find(_.name == param.get.name).get.id))) //Return Class
    } else {

      val dividedSubsets: Map[String, Vector[TrainingInstance]] =
        splitByParam(subset, bestParameterToDivide()._1)

      val newNodes: Map[Vector[TrainingInstance], Node] = dividedSubsets.map {
        case (pName, newSet) =>
          newSet -> Node(Some(pName), None)()
      }

      println(s"Divided into: ${newNodes.prettyPrint}")

      Node(
        Some(bestParameterToDivide()._1.name),
        None
      )(
        newNodes.map {
          case (newSet, node: Tree) => {
            node.traverse(node)(println)
            divide_and_conquer(names, newSet, node, Some(bestParameterToDivide()._1))
          }
        }.toSeq: _*
      )
    }

  }

  def splitByParam(subset: Vector[TrainingInstance],
                   param: Param): Map[String, Vector[TrainingInstance]] =
    subset.groupBy(identity((x: TrainingInstance) => x(param.id)))

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

  def recognition(rulesTree: Tree) = {
    classify(paramNames, testValues.head, rulesTree)
  }

  def classify(names: Vector[Param], trainingInstance: TrainingInstance, node: Tree): String = {
    node match {
      case Leaf(value, _) => value.get
      case Node(paramThatSplits: Option[String], _, children: Seq[Tree]) =>

        val paramConverted: Param = names.find(_.name == paramThatSplits.get).get

        val paramValue = trainingInstance(paramConverted.id)

        val neededChildNode = children.filter { c =>
          (for {
            childParam: String <- c.value
            neededParam: String <- paramThatSplits
          } yield childParam == neededParam).get
        }.head

        classify(names, trainingInstance, neededChildNode)
    }
  }

  //https://stackoverflow.com/questions/32004050/pretty-print-a-nested-map-in-scala
  implicit class PrettyPrintMap[K, V](val map: Map[K, V]) {
    def prettyPrint: PrettyPrintMap[K, V] = this

    override def toString: String = {
      val valuesString = toStringLines.mkString("\n")

      "Map (\n" + valuesString + "\n)"
    }

    def toStringLines = {
      map
        .flatMap { case (k, v) => keyValueToString(k, v) }
        .map(indentLine(_))
    }

    def keyValueToString(key: K, value: V): Iterable[String] = {
      value match {
        case v: Map[_, _] =>
          Iterable(key + " -> Map (") ++ v.prettyPrint.toStringLines ++ Iterable(
            ")")
        case x => Iterable(key + " -> " + x.toString)
      }
    }

    def indentLine(line: String): String = {
      "\t" + line
    }
  }

  println("----- Learning begin -----")
  val resultTree = learning()
  println("----- Learning end -----")
  resultTree.traverse(resultTree)(println)
  println("----- Start recognition -----")
  val testSet = recognition(resultTree)
  println(testSet)

}
