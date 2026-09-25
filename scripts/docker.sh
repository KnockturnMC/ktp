#!/usr/bin/env bash
MINECRAFT_VERSION="$(./gradlew -q printMinecraftVersion)"
docker buildx build -f .docker/Dockerfile . --target runner-jbr -t "docker.knockturnmc.com/ktp/ktp-server:$MINECRAFT_VERSION-jbr"
docker buildx build -f .docker/Dockerfile . --target runner-corretto -t "docker.knockturnmc.com/ktp/ktp-server:$MINECRAFT_VERSION"
