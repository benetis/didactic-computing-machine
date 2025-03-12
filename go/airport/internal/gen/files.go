package gen

import (
	"fmt"
	"go/parser"
	"go/token"
	"os"
	"path/filepath"
	"strings"
)

func loadFolderRecursively(folder string) []ParsedFile {
	fset := token.NewFileSet()
	var parsedFiles []ParsedFile

	err := filepath.Walk(folder, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if !info.IsDir() && strings.HasSuffix(path, ".go") {
			file, err := parser.ParseFile(fset, path, nil, parser.ParseComments)
			if err != nil {
				return fmt.Errorf("failed to parse file %s: %w", path, err)
			}
			parsedFiles = append(parsedFiles, ParsedFile{
				Path: path,
				File: file,
			})
		}
		return nil
	})
	if err != nil {
		panic(fmt.Errorf("failed to walk directory %s: %w", folder, err))
	}
	return parsedFiles
}
