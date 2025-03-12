package gen

import "fmt"

func validateDefinitions(defs []AircraftDef) {
	dbMap := make(map[uint16]string)
	showMap := make(map[string]string)
	for _, def := range defs {
		if existing, ok := dbMap[def.DB]; ok {
			panic(fmt.Errorf("duplicate %s() definition: DB value %d is defined for both %s and %s", dBFunc, def.DB, existing, def.Type))
		}
		dbMap[def.DB] = def.Type

		if existing, ok := showMap[def.String]; ok {
			panic(fmt.Errorf("duplicate %s() definition: String value %q is defined for both %s and %s", showFunc, def.String, existing, def.Type))
		}
		showMap[def.String] = def.Type
	}
}
