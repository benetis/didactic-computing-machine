package internal

import (
	"fmt"
	"io"
	"net/http"
)

func EchoHandler(debug bool) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		body, err := io.ReadAll(r.Body)
		if err != nil {
			http.Error(w, "Failed to read request body", http.StatusInternalServerError)
			return
		}
		defer r.Body.Close()

		if debug {
			fmt.Printf("Echoing request: %s\n", body)
		}

		w.WriteHeader(http.StatusOK)
		_, err = w.Write(body)
		if err != nil {
			http.Error(w, "Failed to write response", http.StatusInternalServerError)
			fmt.Println("Failed to write response")
			return
		}
	}
}
