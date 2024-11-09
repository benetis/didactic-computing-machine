package loadbalancer

import (
	"io"
	"log"
	"net/http"
	"net/url"
	"sync/atomic"
)

type Container struct {
	backendURLs []string
	counter     uint32
}

func NewLoadBalancer(backendURLs []string) *Container {
	return &Container{
		backendURLs: backendURLs,
		counter:     0,
	}
}

func (lb *Container) nextRoundRobin() string {
	index := atomic.AddUint32(&lb.counter, 1)
	robinIndex := (int(index) - 1) % len(lb.backendURLs)

	return lb.backendURLs[robinIndex]
}

func (lb *Container) HandleRequest(w http.ResponseWriter, r *http.Request) string {
	backendURL := lb.nextRoundRobin()

	parsedURL, err := url.Parse(backendURL)
	if err != nil {
		http.Error(w, "Invalid backend URL", http.StatusInternalServerError)
		log.Printf("Error parsing backend URL: %v", err)
		return "error-"
	}
	parsedURL.Path = r.URL.Path

	forwardRequest(w, r, parsedURL.String())
	return backendURL
}

func forwardRequest(w http.ResponseWriter, r *http.Request, destinationURL string) {
	req, err := http.NewRequest(r.Method, destinationURL, r.Body)
	if err != nil {
		http.Error(w, "Failed to create request", http.StatusInternalServerError)
		log.Printf("Error creating request: %v", err)
		return
	}

	req.Header = r.Header.Clone()

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		http.Error(w, "Bad Gateway", http.StatusBadGateway)
		log.Printf("Error forwarding request: %v", err)
		return
	}
	defer resp.Body.Close()

	for k, v := range resp.Header {
		w.Header()[k] = v
	}
	w.WriteHeader(resp.StatusCode)

	_, err = io.Copy(w, resp.Body)
	if err != nil {
		http.Error(w, "Service Unavailable", http.StatusServiceUnavailable)
		log.Printf("Error copying response: %v", err)
	}
}
