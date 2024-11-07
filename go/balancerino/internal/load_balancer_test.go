package internal

import (
	"github.com/stretchr/testify/assert"
	"io"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestRoundRobinRouting(t *testing.T) {
	backend1 := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte("Response from backend 1"))
	}))
	defer backend1.Close()

	backend2 := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte("Response from backend 2"))
	}))
	defer backend2.Close()

	lb := NewLoadBalancer([]string{backend1.URL, backend2.URL})

	tests := []struct {
		expectedResponse string
	}{
		{"Response from backend 1"},
		{"Response from backend 2"},
		{"Response from backend 1"},
		{"Response from backend 2"},
	}

	for i, tt := range tests {
		req := httptest.NewRequest(http.MethodGet, "/", nil)
		w := httptest.NewRecorder()

		lb.HandleRequest(w, req)

		resp := w.Result()
		body, _ := io.ReadAll(resp.Body)

		assert.Equal(t, tt.expectedResponse, string(body), "Test %d: Response does not match expected", i+1)
	}
}
