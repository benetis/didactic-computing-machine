package narrow

import (
	"airport/internal/aircraft/params"
	"fmt"
)

// @Aircraft
type Boeing737 struct {
	Model        string
	FuelCapacity params.Liter
	RunwayLength params.Meter
}

func (b Boeing737) String() string {
	return "boeing_737"
}

func (b Boeing737) DB() uint16 {
	return 10
}

func (b Boeing737) TakeOff() {
	fmt.Printf("Taking off %s with %d liters of fuel\n", b.String(), b.FuelCapacity)
}
