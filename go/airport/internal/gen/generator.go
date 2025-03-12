package gen

import (
	"fmt"
	"go/ast"
	"path/filepath"
)

const showFunc = "String"
const dBFunc = "DB"
const marker = "@Aircraft"

type AircraftDef struct {
	ImportPath string // the import path of the package where the type is defined.
	Package    string // the package name where the type is defined.
	Type       string // the concrete type (e.g. Boeing737).
	String     string // the value returned by String(), e.g. "boeing_737". Used for encoding and decoding.
	DB         uint16 // database representation for the aircraft type.
}

type ImportData struct {
	Package string
	Path    string // import path (e.g. "internal/aircraft/narrow")
}

type TemplateData struct {
	Imports  []ImportData
	Aircraft []AircraftDef
}

type ParsedFile struct {
	Path string
	File *ast.File
}

func Generate() {
	const folder = "internal/aircraft"
	const outputFile = "internal/aircraft/aircraft_gen.go"
	const generatedPackageName = "aircraft"
	const modulePrefix = "airport"

	parsedFiles := loadFolderRecursively(folder)

	var definitions []AircraftDef

	for _, parsedFile := range parsedFiles {
		annotations := collectAnnotations(parsedFile.File, marker)
		defs := buildAircraftDefinitions(
			parsedFile,
			folder,
			annotations,
			modulePrefix,
			generatedPackageName,
		)
		definitions = append(definitions, defs...)
	}

	validateDefinitions(definitions)
	imports := calculateImports(definitions, generatedPackageName)

	output(outputFile, TemplateData{
		Imports:  imports,
		Aircraft: definitions,
	})

	fmt.Printf("Generated %s with %d defintions\n", outputFile, len(definitions))
}

func buildAircraftDefinitions(
	pf ParsedFile,
	baseFolder string,
	annotated map[string]string,
	modulePrefix string,
	generatedPackageName string,
) []AircraftDef {
	showValues := collectStringDefinitions(pf.File, annotated)
	dbValues := collectDBDefinitions(pf.File)
	pkg := pf.File.Name.Name

	dir := filepath.Dir(pf.Path)
	importPath := ""
	rel, err := filepath.Rel(baseFolder, dir)
	if err == nil && rel != "." {
		importPath = filepath.ToSlash(filepath.Join(modulePrefix, baseFolder, rel))
	}

	var defs []AircraftDef
	for typ := range annotated {
		defs = append(defs, AircraftDef{
			ImportPath: func() string {
				if pkg == generatedPackageName {
					return ""
				}
				return importPath
			}(),
			Package: pkg,
			Type:    typ,
			String:  showValues[typ],
			DB:      dbValues[typ],
		})
	}
	return defs
}

func calculateImports(defs []AircraftDef, currentPackage string) []ImportData {
	importMap := make(map[string]string) // importPath -> package name
	for _, def := range defs {
		if def.ImportPath != "" && def.Package != currentPackage {
			importMap[def.ImportPath] = def.Package
		}
	}
	var imports []ImportData
	for path, pkg := range importMap {
		imports = append(imports, ImportData{
			Package: pkg,
			Path:    path,
		})
	}
	return imports
}
