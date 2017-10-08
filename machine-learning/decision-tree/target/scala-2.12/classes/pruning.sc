
val zAlpha = 1.14999997615814

def pessimisticErr(nodeN: Int, nodeNMax: Int): Double = {
  val E: Double = nodeN - nodeNMax

  val p: Double = (E + 1) / (nodeN + 2)

  nodeN * (p + zAlpha * math.sqrt(p * (1.0 - p) / (nodeN + 2)))
}

val b = pessimisticErr(3, 2)

val bb = pessimisticErr(1, 1)
val bc = pessimisticErr(2, 2)

val bChildren = bb + bc

val pruneBChildren = b < bChildren

val c = pessimisticErr(5, 4)

val cd = pessimisticErr(3, 3)
val cc = pessimisticErr(1, 1)
val cb = pessimisticErr(1, 1)

val cChildren = cd + cc + cb

val pruneCChildren = c < cChildren

val d = pessimisticErr(2, 1)

val db = pessimisticErr(1, 1)
val dc = pessimisticErr(1, 1)

val dChildren = db + dc

val pruneDChildren = d < dChildren
