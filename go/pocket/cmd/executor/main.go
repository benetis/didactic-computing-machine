package main

import (
	"fmt"
	"os"
	"path/filepath"
	"pocket/internal/prepare"
)

func main() {
	if len(os.Args) < 2 {
		fmt.Println("Usage: pocket <command> [options]")
		os.Exit(1)
	}

	command := os.Args[1]

	switch command {
	case "run":
		if len(os.Args) < 3 {
			fmt.Println("Usage: pocket run <path>")
			os.Exit(1)
		}
		pathStr := os.Args[2]
		path := filepath.FromSlash(pathStr)
		fmt.Println("Running job...")
		prepare.Run(path)
	case "tar":
		if len(os.Args) < 3 {
			fmt.Println("Usage: pocket tar <path>")
			os.Exit(1)
		}
		path := os.Args[2]
		err := prepare.Tar(path)
		if err != nil {
			fmt.Printf("Error creating tarball: %v\n", err)
			os.Exit(1)
		}
		fmt.Printf("Created tarball from: %s\n", path)

	default:
		fmt.Printf("Unknown command: %s\n", command)
		os.Exit(1)
	}
}
