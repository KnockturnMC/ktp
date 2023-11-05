# The builder image, responsible for building the project from source state.
# 21
FROM amazoncorretto:sha256:f0c381e971cab629f1a65d0cf6d8e873cfd246b94b676e4846f00f58df1749c8 as builder
RUN yum install -y git
RUN git config --global user.name "ci"; git config --global user.email "ci@ci.ci"

WORKDIR /work

# Copy gradle wrapper and configuration files
COPY *gradle.kts gradle.properties gradlew* ./
COPY gradle ./gradle

# Copy patches and build data
COPY patches/ patches/
COPY build-data/ build-data/

# Apply all patches to make
RUN ./gradlew applyPatches --no-daemon

# Copy over git information of repository needed during build
COPY .git .git

# Compile ktp to paperclip jar
RUN ./gradlew createReobfPaperclipJar --no-daemon

RUN cp build/libs/ktp-paperclip-$(cat gradle.properties | grep 'version=' | cut -d'=' -f2)-reobf.jar server.jar

# Runtime image
# 21-alpine3.19
FROM amazoncorretto:sha256:02248922f536b975c5f1fc00c36e6bebfb4bae5b95eaf77f034251b47abd3954

# Configure non root user for server
ARG USERNAME=server
ARG USER_UID=1000
ARG USER_GID=$USER_UID

WORKDIR /home/$USERNAME

RUN apk add --no-cache shadow font-dejavu \
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
