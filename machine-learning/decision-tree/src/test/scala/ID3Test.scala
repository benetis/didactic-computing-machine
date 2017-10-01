import ID3.{Leaf, Node, Param}
import org.scalatest.FreeSpec

/**
  * Some examples for tests taken from Gailius Raskinis Machine learning course @VDU
  */
class ID3Test extends FreeSpec {

  "Divide and conquer" - {
    "return same class as element if passed one trainingSet" in {

      assert(
        ID3.divide_and_conquer(
          Vector(
            Param(0, "p"),
            Param(1, "c")
          ),
          Vector(Vector("param1", "class")),
          Node(None, None)(),
          Some("param1")) === Leaf(Some("class"), Some("param1"), "param1"))
    }

    "return nodes attached to root node if passed few training sets" in {

      assert(
        ID3.divide_and_conquer(
          Vector(Param(0, "instanceName"),
                 Param(1, "size"),
                 Param(2, "hair"),
                 Param(3, "eyes"),
                 Param(4, "class")),
          Vector(
            Vector("e1", "m", "g", "m", "+"),
            Vector("e2", "d", "g", "r", "-"),
            Vector("e3", "d", "r", "m", "+"),
            Vector("e4", "m", "j", "m", "-"),
            Vector("e5", "d", "j", "m", "-"),
            Vector("e6", "d", "g", "m", "+"),
            Vector("e7", "j", "r", "-"),
            Vector("e8", "m", "g", "r", "-")
          ),
          Node(None, None)()
        ) ===
          Node(
            Some("eyes"),
            None,
            List(
              Node(Some("hair"),
                   Some("m"),
                   List(Leaf(Some("-"), Some("j"), "e4"),
                        Leaf(Some("+"), Some("g"), "e1"),
                        Leaf(Some("+"), Some("r"), "e3"))),
              Leaf(Some("-"), Some("r"), "e2"),
              Leaf(Some("-"), Some("-"), "e7")
            )
          )
      )
    }

    "should correctly create tree" in {
      assert(
        ID3.divide_and_conquer(
          Vector(
            Param(0, "instanceName"),
            Param(1, "Hair"),
            Param(2, "Size"),
            Param(3, "Class")
          ),
          Vector(
            Vector("e1", "short", "small", "chihuahua"),
            Vector("e2", "long", "big", "shepherd"),
            Vector("e3", "fluff", "small", "corgi"),
            Vector("e4", "long", "small", "corgi"),
            Vector("e5", "short", "big", "greyhound")
          ),
          Node(None, None)()
        ) === Node(
          Some("Size"),
          None,
          List(
            Node(
              Some("Hair"),
              Some("small"),
              List(Leaf(Some("chihuahua"), Some("short"), "e1"),
                   Leaf(Some("corgi"), Some("long"), "e4"),
                   Leaf(Some("corgi"), Some("fluff"), "e3"))
            ),
            Node(Some("Hair"),
                 Some("big"),
                 List(Leaf(Some("greyhound"), Some("short"), "e5"),
                      Leaf(Some("shepherd"), Some("long"), "e2")))
          )
        )
      )
    }

  }

  "Information gain" - {

    "entropy of two different elements should not be 0" in {
      assert(
        ID3.entropy(
          Vector(
            Vector("1", "2", "A"),
            Vector("2", "4", "B"),
            Vector("1", "4", "B")
          )
        ) !== 0.0d)
    }

    "entropy of two same elements should be 0" in {
      assert(
        ID3.entropy(
          Vector(
            Vector("1", "2", "A"),
            Vector("1", "2", "A")
          )
        ) === 0.0d)
    }

    "should count entropy to be 0.95 as in example for size" in {
      assert(
        math.round(
          ID3.entropy(
            Vector(
              Vector("m", "+"),
              Vector("d", "-"),
              Vector("d", "+"),
              Vector("m", "-"),
              Vector("d", "-"),
              Vector("d", "+"),
              Vector("d", "-"),
              Vector("m", "-")
            )) * 100.0) / 100.0 === 0.95)
    }

    "should calculate information gain correctly" in {}

  }

  "Split by param" - {

    "should split into correct subsets according" in {
      assert(
        ID3.splitByParam(
          Vector(
            Vector("m", "g", "+"),
            Vector("d", "g", "-"),
            Vector("d", "r", "+"),
            Vector("m", "j", "-"),
            Vector("d", "j", "-"),
            Vector("d", "g", "+"),
            Vector("d", "j", "-"),
            Vector("m", "g", "-")
          ),
          Param(1, "hair")
        ) === Map(
          "g" -> Vector(
            Vector("m", "g", "+"),
            Vector("d", "g", "-"),
            Vector("d", "g", "+"),
            Vector("m", "g", "-")
          ),
          "r" -> Vector(
            Vector("d", "r", "+")
          ),
          "j" -> Vector(
            Vector("m", "j", "-"),
            Vector("d", "j", "-"),
            Vector("d", "j", "-")
          )
        )
      )
    }
  }

  "Classify" - {
    "should classify to - with specified tree" in {
      assert(
        ID3.classify(
          Vector(Param(0, "size"), Param(1, "hair"), Param(2, "eyes")),
          Vector("m", "j", "r"),
          Node(
            Some("hair"),
            None,
            List(Leaf(Some("-"), Some("j")),
                 Node(Some("eyes"),
                      None,
                      List(Leaf(Some("+"), Some("m")),
                           Leaf(Some("-"), Some("r")))),
                 Leaf(Some("+"), Some("r")))
          )
        ) === "-"
      )
    }

    "should classify to be corgi with specified tree and test input" in {
      assert(
        ID3.classify(
          Vector(
            Param(0, "Hair"),
            Param(1, "Size")
          ),
          Vector("fluff", "small"),
          Node(
            Some("Size"),
            None,
            List(
              Node(Some("Hair"),
                   Some("small"),
                   List(Leaf(Some("chihuahua"), Some("short")),
                        Leaf(Some("corgi"), Some("long")),
                        Leaf(Some("corgi"), Some("fluff")))),
              Node(Some("Hair"),
                   Some("big"),
                   List(Leaf(Some("greyhound"), Some("short")),
                        Leaf(Some("shepherd"), Some("long"))))
            )
          )
        ) === "corgi"
      )
    }

  }
}
