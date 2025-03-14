package gen

const codeTemplate = `// Code generated by aircraft generator. DO NOT EDIT.
package aircraft

{{ if .Imports }}
import (
{{- range .Imports }}
	"{{ .Path }}"
{{- end }}
)
{{ end }}

type Aircraft interface {
	TakeOff()
	String() string // This cannot be changed unless all encodes, decoders and contracts are changed as well.
	DB() uint16 // This cannot be changed unless DB data is changed as well.
}

var AircraftRegistry = map[string]func() Aircraft{
{{- range .Aircraft }}
	"{{ .String }}": func() Aircraft { return {{ CalculatedType . }}{} },
{{- end }}
}

var AircraftDBRegistry = map[uint16]func() Aircraft{
{{- range .Aircraft }} 
	{{ .DB }}: func() Aircraft { return {{ CalculatedType . }}{} },
{{- end }}
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
`
