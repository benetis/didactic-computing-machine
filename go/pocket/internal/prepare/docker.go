package prepare

import (
	"context"
	"fmt"
	"os"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/client"
	"github.com/docker/docker/pkg/stdcopy"
)

func main() {
	config, err := loadConfig("pocket.yml")
	if err != nil {
		panic(err)
	}

	if buildStep, ok := config.Steps["build"]; ok {
		err = executeStep(buildStep)
		if err != nil {
			panic(err)
		}
	} else {
		fmt.Println("Build step not found in the configuration.")
	}
}

func executeStep(step Step) error {
	ctx := context.Background()
	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		return err
	}

	_, err = cli.ImagePull(ctx, step.Image, types.ImagePullOptions{})
	if err != nil {
		return err
	}

	resp, err := cli.ContainerCreate(ctx, &container.Config{
		Image: step.Image,
		Cmd:   step.Cmds,
		Tty:   false,
	}, nil, nil, nil, "")
	if err != nil {
		return err
	}

	// Start the container.
	if err := cli.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{}); err != nil {
		return err
	}

	// Wait for the container to finish.
	statusCh, errCh := cli.ContainerWait(ctx, resp.ID, container.WaitConditionNotRunning)
	select {
	case err := <-errCh:
		if err != nil {
			return err
		}
	case <-statusCh:
	}

	// Capture and log the container output.
	out, err := cli.ContainerLogs(ctx, resp.ID, types.ContainerLogsOptions{ShowStdout: true, ShowStderr: true})
	if err != nil {
		return err
	}
	stdcopy.StdCopy(os.Stdout, os.Stderr, out)

	// Cleanup: Remove the container.
	defer cli.ContainerRemove(ctx, resp.ID, types.ContainerRemoveOptions{
		Force: true,
	})

	return nil
}
