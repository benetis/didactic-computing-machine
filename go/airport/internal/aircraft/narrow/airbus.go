package narrow

import (
	"airport/internal/aircraft/params"
	"fmt"
)

// @Aircraft
type AirbusA320 struct {
	Model          string
	PassengerCount int
	RunwayLength   params.Meter
}

func (a AirbusA320) TakeOff() {
	fmt.Printf("Taking off %s with %d passengers\n", a.Show(), a.PassengerCount)
}

func (a AirbusA320) Show() string {
	return "airbus_a320"
}

func (a AirbusA320) DB() uint16 {
	return 5
}
