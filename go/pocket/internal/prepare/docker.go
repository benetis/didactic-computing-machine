package prepare

import (
	"context"
	"fmt"
	"os"
	"path/filepath"
	"strings"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/client"
	"github.com/docker/docker/pkg/stdcopy"
)

func Run(workingDir string) {
	absWorkingDir, err := filepath.Abs(workingDir)
	if err != nil {
		panic(fmt.Errorf("failed to get absolute path: %w", err))
	}
	configFilePath := filepath.Join(workingDir, "pocket.yml")
	config, err := loadConfig(configFilePath)
	if err != nil {
		panic(err)
	}

	if buildStep, ok := config.Steps["build"]; ok {
		err = executeStep(*buildStep, absWorkingDir)
		if err != nil {
			panic(err)
		}
	} else {
		fmt.Println("Build step not found in the configuration.")
	}
}

func executeStep(step Step, workingDir string) error {
	ctx := context.Background()
	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		return err
	}

	_, err = cli.ImagePull(ctx, step.Image, types.ImagePullOptions{})
	if err != nil {
		return err
	}

	fullCmd := strings.Join(step.Cmds, " && ")

	resp, err := cli.ContainerCreate(ctx, &container.Config{
		Image:      step.Image,
		Cmd:        []string{"/bin/sh", "-c", fullCmd},
		Tty:        false,
		WorkingDir: "/app",
	}, &container.HostConfig{
		Binds: []string{fmt.Sprintf("%s:/app", workingDir)},
	}, nil, nil, "")
	if err != nil {
		return err
	}

	if err := cli.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{}); err != nil {
		return err
	}

	statusCh, errCh := cli.ContainerWait(ctx, resp.ID, container.WaitConditionNotRunning)
	select {
	case err := <-errCh:
		if err != nil {
			return err
		}
	case <-statusCh:
	}

	out, err := cli.ContainerLogs(ctx, resp.ID, types.ContainerLogsOptions{ShowStdout: true, ShowStderr: true})
	if err != nil {
		return err
	}
	stdcopy.StdCopy(os.Stdout, os.Stderr, out)

	defer cli.ContainerRemove(ctx, resp.ID, types.ContainerRemoveOptions{
		Force: true,
	})

	return nil
}
