# The builder image, responsible for building the project from source state.
FROM amazoncorretto:21 as builder
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
RUN ./gradlew applyPatches

# Copy over git information of repository needed during build
COPY .git .git

# Compile ktp to paperclip jar
RUN ./gradlew createReobfPaperclipJar

RUN cp build/libs/ktp-paperclip-$(cat gradle.properties | grep 'version=' | cut -d'=' -f2)-reobf.jar server.jar

# Runtime image, jre only
FROM amazoncorretto:21-alpine3.19

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

ENTRYPOINT ["sh", "/bin/run.sh"]
