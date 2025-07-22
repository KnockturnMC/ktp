# syntax=docker/dockerfile-upstream:master-labs <- required for --parents in COPY

ARG ALPINE=alpine@sha256:4bcff63911fcb4448bd4fdacec207030997caf25e9bea4045fa6c8c44de311d1
ARG CORRETTO=amazoncorretto@sha256:670e3459f1bb0491c4a594ec23f8383acf426ee2e54419ac34a827cb276aa5ed
# The builder image, responsible for building the project from source state.
# 21
FROM ${CORRETTO} AS builder
RUN yum install -y git
RUN git config --global user.name "ci"; git config --global user.email "ci@ci.ci"

WORKDIR /work

# Copy the minimal amount of files needed to first download and patch a 0 patch fork.
# This way, buildx can cache the base fork setup/decompilation steps etc.
# Improves build speeds in case only code was changed.
COPY --parents build-data gradle.properties gradlew gradle/ build.gradle.kts settings.gradle.kts **/*.kts.patch .

# Apply 0 fork patches to cache fork setup
RUN ./gradlew applyAllPatches --no-daemon

# Copy over the entire rest of the repo needed for a proper build.
COPY --parents .git ktp-api ktp-server .

# Compile ktp to paperclip jar
RUN ./gradlew applyAllPatches --no-daemon; ./gradlew createMojmapPaperclipJar --no-daemon

RUN cp ktp-server/build/libs/ktp-paperclip-$(cat gradle.properties | grep 'version=' | cut -d'=' -f2)-mojmap.jar server.jar

# Standalone image creating files for installing corretto
FROM ${ALPINE} AS corretto-installer

ARG CORRETTO_VERSION=amazon-corretto-21-x64-alpine-jdk.tar.gz
RUN wget https://corretto.aws/downloads/latest/${CORRETTO_VERSION}; \
    mkdir -p /usr/lib/jvm; \
    tar -xzvf ${CORRETTO_VERSION} --strip-components=1 -C /usr/lib/jvm;

# Standalone image creating files for installing jetbrains runtime (uses musl as we are running on alpine)
FROM ${ALPINE} AS jbr-installer

ARG JBR_VERSION=jbr-21.0.7-linux-musl-x64-b1038.58.tar.gz
RUN wget https://cache-redirector.jetbrains.com/intellij-jbr/${JBR_VERSION}; \
    mkdir -p /usr/lib/jvm; \
    tar -xzvf ${JBR_VERSION} --strip-components=1 -C /usr/lib/jvm;

# Runtime image
# alpine 3.22.1
FROM ${ALPINE} AS runner-base

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

# The final image, copying over the jbr binaries
FROM runner-base AS runner-jbr
COPY --from=jbr-installer /usr/lib/jvm /usr/lib/jvm

# The final image, copying over the corretto binaries
FROM runner-base AS runner-corretto
COPY --from=corretto-installer /usr/lib/jvm /usr/lib/jvm
