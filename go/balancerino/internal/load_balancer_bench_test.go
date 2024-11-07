package internal

import (
	"bytes"
	"io"
	"net/http"
	"net/http/httptest"
	"testing"
)

// Synthetic benchmark
func BenchmarkHandleRequestParallel(b *testing.B) {
	lb := NewLoadBalancer([]string{
		"http://localhost:9001",
		"http://localhost:9002",
	})

	reqBody := "Benchmarking with real echo servers!"
	req := httptest.NewRequest(http.MethodPost, "/", bytes.NewBufferString(reqBody))
	req.Header.Set("Content-Type", "text/plain")

	b.RunParallel(func(pb *testing.PB) {
		for pb.Next() {
			w := httptest.NewRecorder()

			lb.HandleRequest(w, req)

			resp := w.Result()
			_, err := io.ReadAll(resp.Body)
			resp.Body.Close()
			if err != nil {
				b.Fatalf("Error reading response: %v", err)
			}
		}
	})
}
