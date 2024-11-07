package main

import (
	"balancerino/internal"
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"
)

func main() {
	port := flag.String("port", "9001", "Echo server port")
	debug := flag.Bool("debug", false, "Enable debug mode")
	flag.Parse()

	if *port == "" {
		fmt.Println("Usage: echo -port=9001")
		os.Exit(1)
	}

	http.HandleFunc("/", internal.EchoHandler(*debug))

	fmt.Printf("Echo server listening on port %s\n", *port)
	log.Fatal(http.ListenAndServe(":"+*port, nil))
}
