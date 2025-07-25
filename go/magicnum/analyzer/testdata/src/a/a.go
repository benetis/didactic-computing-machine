package a

func f() {
	x := 42    // want "magic number detected"
	y := -1    // ok, skip
	z := 0 + 7 // want "magic number detected"
}
