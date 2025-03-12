package gen

import (
	"go/ast"
	"go/token"
	"strconv"
	"strings"
)

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

func collectDBDefinitions(file *ast.File) map[string]uint16 {
	dbValues := make(map[string]uint16)
	for _, decl := range file.Decls {
		funcDecl, ok := decl.(*ast.FuncDecl)
		if !ok || funcDecl.Recv == nil || funcDecl.Name.Name != dBFunc {
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

func collectStringDefinitions(file *ast.File, annotated map[string]string) map[string]string {
	copied := make(map[string]string)
	for k, v := range annotated {
		copied[k] = v
	}
	for _, decl := range file.Decls {
		funcDecl, ok := decl.(*ast.FuncDecl)
		if !ok || funcDecl.Recv == nil || funcDecl.Name.Name != showFunc {
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
