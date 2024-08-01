# The builder image, responsible for building the project from source state.
# 21
FROM amazoncorretto@sha256:f6267db88bddd9a7ae29e59d5b3b51eb4b16167b179d57b27e74a9834e2be29d AS builder
RUN yum install -y git
RUN git config --global user.name "ci"; git config --global user.email "ci@ci.ci"

WORKDIR /work

# Copy gradle wrapper and configuration files
COPY *gradle.kts gradle.properties gradlew* ./
COPY gradle ./gradle

# Copy patches and build data and create empty patch files.
COPY build-data/ build-data/
RUN mkdir -p patches/api patches/server

# Apply all patches to make
RUN ./gradlew applyPatches --no-daemon

# Copy over git information of repository needed during build
# Including patches
COPY .git .git
COPY patches/ patches/

# Compile ktp to paperclip jar
RUN rm -rf ktp-api ktp-server; ./gradlew applyPatches --no-daemon; ./gradlew createReobfPaperclipJar --no-daemon

RUN cp build/libs/ktp-paperclip-$(cat gradle.properties | grep 'version=' | cut -d'=' -f2)-reobf.jar server.jar

# Runtime image
# 21-alpine3.19
FROM amazoncorretto@sha256:c815b95bbbe1eb4f14f9db14884edd1e2c061cb2d3192017476a0d94d611d23d

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
