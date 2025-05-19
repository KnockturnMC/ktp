# syntax=docker/dockerfile-upstream:master-labs <- required for --parents in COPY

# The builder image, responsible for building the project from source state.
# 21
FROM amazoncorretto@sha256:670e3459f1bb0491c4a594ec23f8383acf426ee2e54419ac34a827cb276aa5ed AS builder
RUN yum install -y git
RUN git config --global user.name "ci"; git config --global user.email "ci@ci.ci"

WORKDIR /work

# Copy the minimal amount of files needed to first download and patch a 0 patch fork.
# This way, buildx can cache the base fork setup/decompilation steps etc.
# Improves build speeds in case only code was changed.
COPY --parents gradle.properties gradlew gradle/ build.gradle.kts settings.gradle.kts **/*.kts.patch .

# Apply 0 fork patches to cache fork setup
RUN ./gradlew applyAllPatches --no-daemon

# Copy over the entire rest of the repo needed for a proper build.
COPY --parents .git build-data ktp-api ktp-server .

# Compile ktp to paperclip jar
RUN ./gradlew applyAllPatches --no-daemon; ./gradlew createMojmapPaperclipJar --no-daemon

RUN cp ktp-server/build/libs/ktp-paperclip-$(cat gradle.properties | grep 'version=' | cut -d'=' -f2)-mojmap.jar server.jar

# Runtime image
# 21-alpine3.19
FROM amazoncorretto@sha256:205526a42c31fe002b54b920362e8c8d6b7d2c07ecf54fa00fbaa54ce49626ab

# Configure non root user for server
ARG USERNAME=server
ARG USER_UID=1000
ARG USER_GID=$USER_UID

WORKDIR /home/$USERNAME

RUN apk add --no-cache shadow font-dejavu bash \
    && groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
    && chown -R $USER_UID:$USER_GID /home/$USERNAME
USER $USERNAME

# Expose server ports
EXPOSE 25565/tcp

# Configure start script. The server binary is not located in the main server folder as most of that is mounted in down the line.
ENV SERVER_MEMORY="1G"
COPY .docker/run.sh /bin/run.sh

RUN echo "eula=true" >> eula.txt
COPY --from=builder /work/server.jar /bin/server.jar

ENTRYPOINT ["bash", "/bin/run.sh"]
