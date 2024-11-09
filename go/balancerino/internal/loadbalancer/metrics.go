package loadbalancer

import (
	"fmt"
	"net/http"
	"sync"
	"sync/atomic"
)

type BackendMetrics struct {
	Requests atomic.Uint64
}

type Metrics struct {
	BackendMetrics sync.Map
	TotalRequests  atomic.Uint64
}

func NewMetrics() *Metrics {
	return &Metrics{
		BackendMetrics: sync.Map{},
		TotalRequests:  atomic.Uint64{},
	}
}

func (m *Metrics) WrapHttpHandler(lb *Container) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		m.TotalRequests.Add(1)

		backendURL := lb.HandleRequest(w, r)

		count, found := m.BackendMetrics.Load(backendURL)
		if !found {
			count = &BackendMetrics{}
			m.BackendMetrics.Store(backendURL, count)
		}

		count.(*BackendMetrics).Requests.Add(1)
	}
}

func (m *Metrics) ReportMetrics() {
	fmt.Println("Reporting...")
	fmt.Println("Total Requests: ", m.TotalRequests.Load())

	m.BackendMetrics.Range(func(key, value interface{}) bool {
		fmt.Printf("Backend: %s, Requests: %d\n", key, value.(*BackendMetrics).Requests.Load())
		return true
	})
}
