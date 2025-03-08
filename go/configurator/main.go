package main

import (
	"fmt"
	"os"
	"reflect"
	"strconv"
)

type Config struct {
	Debug bool `env:"DEBUG" default:"false"`
	Port  int  `env:"PORT" default:"8080"`
}

func main() {
	cfg := &Config{}
	err := LoadConfig(cfg)
	if err != nil {
		fmt.Println("Error loading config:", err)
		return
	}

	fmt.Printf("Config: %+v\n", cfg)
}

func LoadConfig(cfg interface{}) error {
	value := reflect.ValueOf(cfg)

	if value.Kind() != reflect.Pointer || value.Elem().Kind() != reflect.Struct {
		return fmt.Errorf("cfg must be a pointer to a struct")
	}

	value = value.Elem()
	structType := value.Type()

	for iter := range structType.NumField() {
		field := structType.Field(iter)
		fieldValue := value.Field(iter)

		envTag := field.Tag.Get("env")
		defaultTag := field.Tag.Get("default")

		osValue := os.Getenv(envTag)
		if osValue == "" {
			osValue = defaultTag
		}

		switch field.Type.Kind() {
		case reflect.Bool:
			boolVal, err := strconv.ParseBool(osValue)
			if err != nil {
				return fmt.Errorf("failed to parse bool value for %s", envTag)
			}
			fieldValue.SetBool(boolVal)
		case reflect.Int, reflect.Int8, reflect.Int16, reflect.Int32, reflect.Int64:
			intVal, err := strconv.ParseInt(osValue, 10, 64)
			if err != nil {
				return fmt.Errorf("failed to parse int value for %s", envTag)
			}
			fieldValue.SetInt(intVal)
		default:
			return fmt.Errorf("unsupported field type %s for %s", field.Type, envTag)
		}

	}

	return nil
}
