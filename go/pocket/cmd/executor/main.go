package main

import (
	"fmt"
	"os"
)

func main() {
	if len(os.Args) < 2 {
		fmt.Println("Usage: pocket <command> [options]")
		os.Exit(1)
	}

	command := os.Args[1]

	switch command {
	case "run":
		fmt.Println("Running job...")
	default:
		fmt.Printf("Unknown command: %s\n", command)
		os.Exit(1)
	}
}
