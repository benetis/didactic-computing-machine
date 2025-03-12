package wide

import "fmt"

// @Aircraft
type AirbusA330 struct {
	Model string
}

func (a AirbusA330) TakeOff() {
	fmt.Printf("Taking off %s\n", a.Show())
}

func (a AirbusA330) Show() string {
	return "airbus_a330"
}

func (a AirbusA330) DB() uint16 {
	return 25
}
