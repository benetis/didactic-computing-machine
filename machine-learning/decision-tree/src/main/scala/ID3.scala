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

  case class Node(value: Option[String],
                  feature: Option[String],
                  children: Seq[Tree])(implicit dummy: Manifest[Node])
      extends Tree

  object Node {
    def apply(value: Option[String], feature: Option[String])(children: Tree*) =
      new Node(value, feature, children)
  }

  case class Leaf(value: Option[String],
                  feature: Option[String],
                  instance: String = "")
      extends Tree

  case class Param(id: Int, name: String)

  val names = Vector("instanceName", "x1", "x2", "x3", "x4", "Class")

  val inputValues = Vector(
    //name x1   x2   x3   x4   Cl
    Vector("e1", "c", "b", "b", "c", "Y"),
    Vector("e2", "d", "c", "b", "c", "G"),
    Vector("e3", "c", "c", "d", "d", "R"),
    Vector("e4", "b", "d", "c", "b", "R"),
    Vector("e5", "d", "c", "d", "c", "G"),
    Vector("e6", "d", "c", "c", "b", "G"),
    Vector("e7", "d", "b", "c", "c", "Y"),
    Vector("e8", "c", "d", "d", "d", "Y"),
    Vector("e9", "b", "b", "b", "b", "R"),
    Vector("e10", "b", "c", "c", "b", "G")
  )

  val testValues = Vector(
    Vector("e11", "b", "b", "c", "d"),
    Vector("e12", "b", "d", "b", "b")
  )

  val paramNames = names.zipWithIndex.map { case (n, i) => Param(i, n) }

  def learning() = {
    val rootNode = Node(None, None)()

    divide_and_conquer(paramNames, inputValues, rootNode)
  }

  def log2(x: Double) = scala.math.log10(x) / scala.math.log10(2.0)

  def divide_and_conquer(
      names: Vector[Param],
      subset: Vector[TrainingInstance],
      node: Tree,
      paramDivided: Option[String] = None
  ): Tree = {
    def hasOnlyOneClass(subset: Vector[TrainingInstance]): Boolean =
      subset.map(_.last).toSet.size == 1

    def bestParameterToDivide(): (Param, Double) = {
      val IGRatioForEachParam: Vector[(Param, Double)] = {
        names.drop(1).dropRight(1).map { p => //dropRight to exclude class and name

          val IG = entropy(subset) - splitByParam(subset, p).map {
            case (paramValue, set: Vector[TrainingInstance]) =>
              val probabilityThisSetIsTaken
                : Double = set.size.toDouble / subset.size
              entropy(set) * probabilityThisSetIsTaken
          }.sum

          val intrinsic = splitByParam(subset, p).foldLeft(0.0d) {
            case (result: Double, (_, iSet: Vector[TrainingInstance])) =>
              result - (iSet.size.toDouble / subset.size) * log2(
                iSet.size.toDouble / subset.size)
          }

          if (intrinsic == 0)
            p -> 0.0d
          else
            p -> IG / intrinsic
        }

      }

      val bestRatio: (Param, Double) = IGRatioForEachParam.maxBy(_._2)

      println(s"Picking $bestRatio")

      bestRatio

    }

    if (hasOnlyOneClass(subset)) {
      Leaf(Some(subset.head.last), paramDivided, subset.head.head) //Return Class
    } else {

      val dividedSubsets: Map[String, Vector[TrainingInstance]] =
        splitByParam(subset, bestParameterToDivide()._1)

      val newNodes: Map[Vector[TrainingInstance], Node] = dividedSubsets.map {
        case (pValue, newSet) =>
          newSet -> Node(Some(pValue), None)()
      }

      println(s"Divided into: ${newNodes.prettyPrint}")

      Node(
        Some(bestParameterToDivide()._1.name),
        paramDivided
      )(
        newNodes.map {
          case (newSet, node: Tree) =>
            node.traverse(node)(println)
            divide_and_conquer(names, newSet, node, node.value)
        }.toSeq: _*
      )
    }

  }

  def splitByParam(subset: Vector[TrainingInstance],
                   param: Param): Map[String, Vector[TrainingInstance]] =
    subset.groupBy(identity((x: TrainingInstance) => x(param.id)))

  def entropy(subset: Vector[TrainingInstance]): Double = {

    val classesCount: Map[String, Vector[String]] =
      subset.map(_.last).groupBy(identity)

    val thisSetSize: Double = classesCount.map(_._2.size).sum

    val entropy = classesCount.foldLeft(0.0d) {
      case (res: Double, (_, iSet: Vector[String])) =>
        res + log2(iSet.size / thisSetSize) * (iSet.size / thisSetSize)
    } * -1

    entropy
  }

  def recognition(rulesTree: Tree) = {
    testValues.map(classify(paramNames, _, rulesTree))
  }

  def classify(names: Vector[Param],
               trainingInstance: TrainingInstance,
               node: Tree): String = {
    node match {
      case Leaf(value, _, _) => value.get
      case Node(paramThatSplits: Option[String], _, children: Seq[Tree]) =>
        val paramConverted: Param =
          names.find(_.name == paramThatSplits.get).get

        val paramValue = trainingInstance(paramConverted.id)

        val neededChildNode: Option[Tree] =
          children.find(_.feature.get == paramValue)

        classify(names, trainingInstance, neededChildNode.get)
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
  println("----- End recognition -----")
  println(testSet)

}
