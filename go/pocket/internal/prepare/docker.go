package prepare

import (
	"context"
	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
	"github.com/docker/docker/pkg/archive"
	"gotest.tools/v3/fs"
	"io"
	"os"
)

func buildDockerImage(contextDir fs.Path) error {
	ctx := context.Background()
	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		return err
	}

	tarPath, err := archive.TarWithOptions(contextDir.Path(), &archive.TarOptions{})
	if err != nil {
		return err
	}
	defer os.Remove(tarPath)

	buildContext, err := os.Open(tarPath)
	if err != nil {
		return err
	}
	defer buildContext.Close()

	options := types.ImageBuildOptions{
		Dockerfile: "Dockerfile",
		Tags:       []string{"your_image_tag"},
		Remove:     true,
	}
	buildResponse, err := cli.ImageBuild(ctx, buildContext, options)
	if err != nil {
		return err
	}
	defer buildResponse.Body.Close()

	_, err = io.Copy(os.Stdout, buildResponse.Body)
	if err != nil {
		return err
	}

	return nil
}
