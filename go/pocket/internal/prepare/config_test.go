package prepare

import (
	"os"
	"path/filepath"
	"testing"
)

func TestLoadConfig(t *testing.T) {
	tests := []struct {
		name        string
		content     string
		expectError bool
	}{
		{
			name: "valid config",
			content: `
steps:
  build:
    image: "golang:1.16"
    cmds:
      - "go build -o myapp"
`,
			expectError: false,
		},
		{
			name:        "invalid yaml",
			content:     `: :`,
			expectError: true,
		},
	}

	for _, tc := range tests {
		t.Run(tc.name, func(t *testing.T) {
			tmpDir := t.TempDir()
			tmpFile := filepath.Join(tmpDir, "config.yml")
			err := os.WriteFile(tmpFile, []byte(tc.content), 0644)
			if err != nil {
				t.Fatalf("Unable to write temporary config file: %v", err)
			}

			_, err = loadConfig(tmpFile)
			if err != nil && !tc.expectError {
				t.Errorf("Expected no error, but got %v", err)
			}
			if err == nil && tc.expectError {
				t.Error("Expected an error, but got none")
			}
		})
	}

	t.Run("non-existing file", func(t *testing.T) {
		_, err := loadConfig("non_existing_file.yml")
		if err == nil {
			t.Error("Expected an error for non-existing file, but got none")
		}
	})
}
