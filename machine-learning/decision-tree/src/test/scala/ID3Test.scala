import ID3.Node
import org.scalatest.FreeSpec

class ID3Test extends FreeSpec {
  "Divide and conquer" - {
    "return same class as element if passed one trainingSet" in {

      assert(ID3.divide_and_conquer(Vector(Vector("P", "K")), Node(None)) === "K")
    }
  }
}
