package gen

import (
	"bytes"
	"fmt"
	"go/format"
	"os"
	"text/template"
)

func output(outputFile string, data TemplateData) {
	out, err := os.Create(outputFile)
	if err != nil {
		panic(fmt.Errorf("failed to create output file: %w", err))
	}
	defer out.Close()

	tmpl, err := template.New("code").Funcs(template.FuncMap{
		// With package.Type or just Type.
		"CalculatedType": func(def AircraftDef) string {
			if def.ImportPath == "" {
				return def.Type
			}
			return def.Package + "." + def.Type
		},
	}).Parse(codeTemplate)
	if err != nil {
		panic(fmt.Errorf("error parsing template: %w", err))
	}
	var buf bytes.Buffer
	if err := tmpl.Execute(&buf, data); err != nil {
		panic(fmt.Errorf("error executing template: %w", err))
	}

	formatted, err := format.Source(buf.Bytes())
	if err != nil {
		panic(fmt.Errorf("error formatting code: %w", err))
	}

	err = os.WriteFile(outputFile, formatted, 0644)
	if err != nil {
		panic(fmt.Errorf("error writing output file: %w", err))
	}
}
