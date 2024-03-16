package prepare

import (
	"archive/tar"
	"compress/gzip"
	"io"
	"os"
	"path/filepath"
)

func Tar(dir string) error {
	var files []string

	err := filepath.Walk(dir, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if info.IsDir() {
			return nil
		}
		files = append(files, path)
		return nil
	})
	if err != nil {
		return err
	}

	tarFile, err := os.Create("context.tar")
	if err != nil {
		return err
	}
	defer tarFile.Close()

	gw := gzip.NewWriter(tarFile)
	defer gw.Close()

	tw := tar.NewWriter(gw)
	defer tw.Close()

	for _, file := range files {
		err = func(file string) error {
			f, err := os.Open(file)
			if err != nil {
				return err
			}
			defer f.Close()

			stat, err := f.Stat()
			if err != nil {
				return err
			}

			hdr := &tar.Header{
				Name: file,
				Mode: int64(stat.Mode()),
				Size: stat.Size(),
			}
			if err := tw.WriteHeader(hdr); err != nil {
				return err
			}

			_, err = io.Copy(tw, f)
			return err
		}(file)
		if err != nil {
			return err
		}
	}

	return nil
}
