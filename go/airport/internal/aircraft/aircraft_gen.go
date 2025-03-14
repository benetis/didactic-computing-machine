// Code generated by aircraft generator. DO NOT EDIT.
package aircraft

import (
	"airport/internal/aircraft/narrow"
	"airport/internal/aircraft/wide"
)

type Aircraft interface {
	TakeOff()
	String() string // This cannot be changed unless all encodes, decoders and contracts are changed as well.
	DB() uint16     // This cannot be changed unless DB data is changed as well.
}

var AircraftRegistry = map[string]func() Aircraft{
	"airbus_a320": func() Aircraft { return narrow.AirbusA320{} },
	"boeing_737":  func() Aircraft { return narrow.Boeing737{} },
	"airbus_a330": func() Aircraft { return wide.AirbusA330{} },
}

var AircraftDBRegistry = map[uint16]func() Aircraft{
	5:  func() Aircraft { return narrow.AirbusA320{} },
	10: func() Aircraft { return narrow.Boeing737{} },
	25: func() Aircraft { return wide.AirbusA330{} },
}

func NewAircraft(typeName string) Aircraft {
	if factory, ok := AircraftRegistry[typeName]; ok {
		return factory()
	}
	return nil
}

func NewAircraftByDB(db uint16) Aircraft {
	if factory, ok := AircraftDBRegistry[db]; ok {
		return factory()
	}
	return nil
}
