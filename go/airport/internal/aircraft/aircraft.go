package aircraft

import "fmt"

//go:generate go run cmd/gen/main.go

type Liter int
type Meter int

// Simulate: Aircraft
type Boeing737 struct {
	Model        string
	FuelCapacity Liter
	RunwayLength Meter
}

func (b Boeing737) Show() string {
	return "boeing_737"
}

func (b Boeing737) TakeOff() {
	fmt.Printf("Taking off %s with %d liters of fuel\n", b.Model, b.FuelCapacity)
}

// Simulate: Aircraft
type AirbusA320 struct {
	Model          string
	PassengerCount int
	RunwayLength   Meter
}

func (a AirbusA320) TakeOff() {
	fmt.Printf("Taking off %s with %d passengers\n", a.Model, a.PassengerCount)
}

func (a AirbusA320) Show() string {
	return "airbus_a320"
}
