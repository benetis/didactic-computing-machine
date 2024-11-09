package main

import (
	"balancerino/internal/loadbalancer"
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"
	"time"
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
	lb := loadbalancer.NewLoadBalancer(backends)
	metrics := loadbalancer.NewMetrics()
	http.HandleFunc("/", metrics.WrapHttpHandler(lb))
	fmt.Printf("Load Balancer started on port %s, distributing to backendURLs: %v\n", *port, backends)

	go func() {
		for {
			metrics.ReportMetrics()
			<-time.After(5 * time.Second)
		}
	}()

	log.Fatal(http.ListenAndServe(":"+*port, nil))
}
