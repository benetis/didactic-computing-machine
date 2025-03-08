package gen

import (
	"bytes"
	"fmt"
	"go/ast"
	"go/format"
	"go/parser"
	"go/token"
	"os"
	"strings"
	"text/template"
)

type TemplateData struct {
	Types []string
}

func Generate() {
	const inputFile = "internal/aircraft.go"
	const outputFile = "internal/aircraft_gen.go"
	const marker = "Simulate: Aircraft"

	file := loadFile(inputFile)
	types := collectAnnotations(file, marker)
	output(outputFile, types)

	fmt.Printf("Generated %s with %d types\n", outputFile, len(types.Types))
}

func output(outputFile string, data TemplateData) {
	out, err := os.Create(outputFile)
	if err != nil {
		panic(fmt.Errorf("failed to create output file: %w", err))
	}
	defer out.Close()

	tmpl, err := template.New("code").Parse(codeTemplate)
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

func collectAnnotations(file *ast.File, marker string) TemplateData {
	types := make([]string, 0)
	for _, decl := range file.Decls {
		genDecl, ok := decl.(*ast.GenDecl)
		if !ok || genDecl.Tok != token.TYPE {
			continue
		}

		if genDecl.Doc != nil {
			found := false
			for _, comment := range genDecl.Doc.List {
				if strings.Contains(comment.Text, marker) {
					found = true
					break
				}
			}

			if found {
				for _, spec := range genDecl.Specs {
					typeSpec, ok := spec.(*ast.TypeSpec)
					if ok {
						types = append(types, typeSpec.Name.Name)
					}
				}
			}
		}

	}

	return TemplateData{Types: types}
}

func loadFile(inputFile string) *ast.File {
	fset := token.NewFileSet()
	file, err := parser.ParseFile(fset, inputFile, nil, parser.ParseComments)
	if err != nil {
		panic(fmt.Errorf("failed to parse input file: %w", err))
	}

	return file
}
