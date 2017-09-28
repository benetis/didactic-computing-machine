import ID3.Node
import org.scalatest.FreeSpec

class ID3Test extends FreeSpec {
  "Divide and conquer" - {
    "return same class as element if passed one trainingSet" in {

      assert(ID3.divide_and_conquer(Vector(Vector("param1", "class")),
        Node(None)) === "class")
    }
  }

  "Information gain" - {

//    "entropy of two different elements should not be 0" in {
//      assert(ID3.entropyOfParam(
//        Vector("x1", "x2", "class"),
//        Vector(
//          Vector("1", "2", "A"),
//          Vector("2", "4", "B"),
//          Vector("1", "4", "B")
//        )
//      ) !== 0)
//    }

//    "two different elements entropy should be 1" in {
//      assert(
//        ID3.averageEntropy(
//          Vector(
//            Vector("1", "2", "A"),
//            Vector("1", "2", "B")
//          ), 2) === 1.0)
//    }

//    "entropy of two same elements should be 0" in {
//      assert(
//        ID3.averageEntropy(
//          Vector(Vector("A", "A")), 1
//        ) === 0.0)
//    }

    "should count information gain to be 0.5 as in example for hair" in {
      assert(
        ID3.informationGain(Vector(
          Vector("g", "+"),
          Vector("g", "-"),
          Vector("r", "+"),
          Vector("j", "-"),
          Vector("j", "-"),
          Vector("g", "+"),
          Vector("j", "-"),
          Vector("g", "-")
        ),
          0) === 0.5)
    }

    "should count information gain to be 0.95 as in example for size" in {
      assert(
        math.round(
          ID3.informationGain(Vector(
            Vector("m", "+"),
            Vector("d", "-"),
            Vector("d", "+"),
            Vector("m", "-"),
            Vector("d", "-"),
            Vector("d", "+"),
            Vector("d", "-"),
            Vector("m", "-")
          ),0) * 100.0)/100.0 === 0.95)
    }

  }
}
