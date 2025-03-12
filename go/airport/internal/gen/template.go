package gen

const codeTemplate = `// Code generated by aircraft generator. DO NOT EDIT.
package aircraft

type Aircraft interface {
	TakeOff()
	Show() string
}

var AircraftRegistry = map[string]func() Aircraft{
{{- range .Aircraft }}
	"{{ .Show }}": func() Aircraft { return {{ .Type }}{} },
{{- end }}
}

func NewAircraft(typeName string) Aircraft {
	if factory, ok := AircraftRegistry[typeName]; ok {
		return factory()
	}
	return nil
}
`
