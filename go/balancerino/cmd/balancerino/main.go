package main

import (
	"balancerino/internal"
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"
)

func main() {
	port := flag.String("port", "8080", "Load balancer port")
	backendsStr := flag.String("backends", "", "Comma separated list of backends. (e.g., http://localhost:9001,http://localhost:9002)")

	flag.Parse()

	if *backendsStr == "" {
		fmt.Println("Usage: balancerino -backends=http://localhost:9001,http://localhost:9002")
		os.Exit(1)
	}

	backends := strings.Split(*backendsStr, ",")
	lb := internal.NewLoadBalancer(backends)

	http.HandleFunc("/", lb.HandleRequest)
	fmt.Printf("Load Balancer started on port %s, distributing to backendURLs: %v\n", *port, backends)
	log.Fatal(http.ListenAndServe(":"+*port, nil))
}
