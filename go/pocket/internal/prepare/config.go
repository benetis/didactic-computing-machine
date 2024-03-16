package prepare

import (
	"gopkg.in/yaml.v3"
	"os"
)

type Config struct {
	Steps map[string]Step `yaml:"steps"`
}

type Step struct {
	Image string   `yaml:"image"`
	Cmds  []string `yaml:"cmds"`
}

func loadConfig(path string) (*Config, error) {
	fileBytes, err := os.ReadFile(path)
	if err != nil {
		return nil, err
	}

	var config Config
	err = yaml.Unmarshal(fileBytes, &config)
	if err != nil {
		return nil, err
	}

	return &config, nil
}
