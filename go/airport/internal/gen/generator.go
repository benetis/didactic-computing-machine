package gen

import (
	"bytes"
	"fmt"
	"go/ast"
	"go/format"
	"go/parser"
	"go/token"
	"os"
	"path/filepath"
	"strconv"
	"strings"
	"text/template"
)

const ShowFunc = "Show"
const DBFunc = "DB"

type AircraftDef struct {
	Package string // the package name where the type is defined.
	Type    string // the concrete type (e.g. Boeing737).
	Show    string // the value returned by Show(), e.g. "boeing_737". Used for encoding and decoding.
	DB      uint16 // database representation for the aircraft type.
}

type TemplateData struct {
	Aircraft []AircraftDef
}

func Generate() {
	const folder = "internal/aircraft"
	const outputFile = "internal/aircraft/aircraft_gen.go"
	const marker = "@Aircraft"

	files := loadFolderRecursively(folder)

	var definitions []AircraftDef

	for _, file := range files {
		annotations := collectAnnotations(file, marker)
		defs := buildAircraftDefinitions(file, annotations)
		definitions = append(definitions, defs...)
	}

	validateDefinitions(definitions)

	output(outputFile, TemplateData{Aircraft: definitions})

	fmt.Printf("Generated %s with %d defintions\n", outputFile, len(definitions))
}

func loadFolderRecursively(folder string) []*ast.File {
	fset := token.NewFileSet()
	var files []*ast.File

	err := filepath.Walk(folder, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}

		if !info.IsDir() && strings.HasSuffix(path, ".go") {
			file, err := parser.ParseFile(fset, path, nil, parser.ParseComments)
			if err != nil {
				return fmt.Errorf("failed to parse file %s: %w", path, err)
			}
			files = append(files, file)
		}
		return nil
	})
	if err != nil {
		panic(fmt.Errorf("failed to walk directory %s: %w", folder, err))
	}
	return files
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

func buildAircraftDefinitions(file *ast.File, annotated map[string]string) []AircraftDef {
	showValues := collectShowDefinitions(file, annotated)
	dbValues := collectDBDefinitions(file)
	pkg := file.Name.Name

	var defs []AircraftDef
	for typ := range annotated {
		defs = append(defs, AircraftDef{
			Package: pkg,
			Type:    typ,
			Show:    showValues[typ],
			DB:      dbValues[typ],
		})
	}
	return defs
}

func collectDBDefinitions(file *ast.File) map[string]uint16 {
	dbValues := make(map[string]uint16)
	for _, decl := range file.Decls {
		funcDecl, ok := decl.(*ast.FuncDecl)
		if !ok || funcDecl.Recv == nil || funcDecl.Name.Name != DBFunc {
			continue
		}
		if len(funcDecl.Recv.List) == 0 {
			continue
		}

		var typeName string
		switch expr := funcDecl.Recv.List[0].Type.(type) {
		case *ast.StarExpr:
			if ident, ok := expr.X.(*ast.Ident); ok {
				typeName = ident.Name
			}
		case *ast.Ident:
			typeName = expr.Name
		}

		if funcDecl.Body != nil && len(funcDecl.Body.List) > 0 {
			if retStmt, ok := funcDecl.Body.List[0].(*ast.ReturnStmt); ok && len(retStmt.Results) > 0 {
				if basicLit, ok := retStmt.Results[0].(*ast.BasicLit); ok && basicLit.Kind == token.INT {
					if intVal, err := strconv.ParseUint(basicLit.Value, 10, 16); err == nil {
						dbValues[typeName] = uint16(intVal)
					}
				}
			}
		}
	}
	return dbValues
}

func collectShowDefinitions(file *ast.File, annotated map[string]string) map[string]string {
	copied := make(map[string]string)
	for k, v := range annotated {
		copied[k] = v
	}
	for _, decl := range file.Decls {
		funcDecl, ok := decl.(*ast.FuncDecl)
		if !ok || funcDecl.Recv == nil || funcDecl.Name.Name != ShowFunc {
			continue
		}
		if len(funcDecl.Recv.List) == 0 {
			continue
		}

		var typeName string
		switch expr := funcDecl.Recv.List[0].Type.(type) {
		case *ast.StarExpr:
			if ident, ok := expr.X.(*ast.Ident); ok {
				typeName = ident.Name
			}
		case *ast.Ident:
			typeName = expr.Name
		}

		if _, exists := copied[typeName]; exists {
			if funcDecl.Body != nil && len(funcDecl.Body.List) > 0 {
				if retStmt, ok := funcDecl.Body.List[0].(*ast.ReturnStmt); ok && len(retStmt.Results) > 0 {
					if basicLit, ok := retStmt.Results[0].(*ast.BasicLit); ok && basicLit.Kind == token.STRING {
						if key, err := strconv.Unquote(basicLit.Value); err == nil {
							copied[typeName] = key
						}
					}
				}
			}
		}
	}

	return copied
}

func collectAnnotations(file *ast.File, marker string) map[string]string {
	annotated := map[string]string{}
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
						annotated[typeSpec.Name.Name] = typeSpec.Name.Name
					}
				}
			}
		}

	}

	return annotated
}

func validateDefinitions(defs []AircraftDef) {
	dbMap := make(map[uint16]string)
	showMap := make(map[string]string)
	for _, def := range defs {
		if existing, ok := dbMap[def.DB]; ok {
			panic(fmt.Errorf("duplicate %s() definition: DB value %d is defined for both %s and %s", DBFunc, def.DB, existing, def.Type))
		}
		dbMap[def.DB] = def.Type

		if existing, ok := showMap[def.Show]; ok {
			panic(fmt.Errorf("duplicate %s() definition: Show value %q is defined for both %s and %s", ShowFunc, def.Show, existing, def.Type))
		}
		showMap[def.Show] = def.Type
	}
}
