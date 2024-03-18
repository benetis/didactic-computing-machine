package prepare

import (
	"gopkg.in/yaml.v3"
	"os"
)

type Config struct {
	StepsOrder []string         `yaml:"steps"`
	Steps      map[string]*Step `yaml:"-"`
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

	var rawConfig struct {
		StepsOrder []string               `yaml:"steps"`
		Steps      map[string]interface{} `yaml:",inline"`
	}
	err = yaml.Unmarshal(fileBytes, &rawConfig)
	if err != nil {
		return nil, err
	}

	config := &Config{
		StepsOrder: rawConfig.StepsOrder,
		Steps:      make(map[string]*Step),
	}

	for _, stepName := range rawConfig.StepsOrder {
		stepData, ok := rawConfig.Steps[stepName]
		if !ok {
			panic("Step not found in the configuration")
		}

		stepBytes, err := yaml.Marshal(stepData)
		if err != nil {
			return nil, err
		}

		var step Step
		err = yaml.Unmarshal(stepBytes, &step)
		if err != nil {
			return nil, err
		}

		config.Steps[stepName] = &step
	}

	return config, nil
}
