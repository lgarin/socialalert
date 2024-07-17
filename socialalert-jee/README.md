# socialalert-jee

Backend for the Snypix application implemented using Quarkus 3.x.

# Docker image

The docker image is based on the dockerfile deliverd with Quarkus. On top of that, the following tools and resources are added to the image:

- exiftool
- ffmpeg
- application logo

## Binaries

All tools and resources are packed in a `bin.tar.xz` before being included in the docker image.

```bash
mkdir bin
```


### exiftool

Download the tool from [exiftool.org](https://exiftool.org/index.html)

```bash
tar xvzf Image-ExifTool-12.87.tar.gz
cp Image-ExifTool-12.87/exiftool bin/
cp -R Image-ExifTool-12.87/lib/ bin/
```

### ffmpeg

Download the static build from [ffmpeg.org](https://ffmpeg.org/download.html#build-linux)

```bash
tar xvJf ffmpeg-release-amd64-static.tar.xz
cp ffmpeg-7.0.1-amd64-static/ffmpeg bin/
```

### logo

```bash
cp logo.jpg bin/
```

### bin.tar.xz

```bash
tar -cvJf bin.tar.xz bin/
```

# PostgreSQL database

### Restore

```bash
pg_restore -U keycloak -d keycloak -c -F t /tmp/dbdata.tar
```
