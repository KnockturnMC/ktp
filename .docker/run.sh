#!/bin/bash

DEBUG_ARG=()
EXTRA_ARGS=()
if $(/usr/lib/jvm/bin/java --version | grep -q 'JBR'); then
    if [ -f "debug" ]; then
        echo "AllowEnhancedClassRedefinition enabled for jbr"
        DEBUG_ARG=("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" "-Ddisable.watchdog=true" "-XX:+AllowEnhancedClassRedefinition" "-Dpaper.playerconnection.keepalive=3600")
    fi
else
    if [ -f "debug" ]; then
        DEBUG_ARG=("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" "-Ddisable.watchdog=true" "-Dpaper.playerconnection.keepalive=3600")
    fi
    EXTRA_ARGS=("--add-modules=jdk.incubator.vector") # Enable vector support on corretto prod builds
fi

SERVER_PATH="/bin/server.jar"
if [ -f ".override_docker_jar" ]; then
    SERVER_PATH="server.jar"
fi

exec /usr/lib/jvm/bin/java -Xms"$SERVER_MEMORY" -Xmx"$SERVER_MEMORY" -XX:+UseCompactObjectHeaders -XX:+AlwaysPreTouch -XX:+DisableExplicitGC \
-XX:+PerfDisableSharedMem -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1HeapRegionSize=8M -XX:G1MaxNewSizePercent=40 \
-XX:G1MixedGCCountTarget=4 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1NewSizePercent=30 -XX:G1RSetUpdatingPauseTimePercent=5 \
-XX:G1ReservePercent=20 -XX:InitiatingHeapOccupancyPercent=15 -XX:MaxTenuringThreshold=1 -XX:SurvivorRatio=32 \
"${EXTRA_ARGS[@]}" "${DEBUG_ARG[@]}" -jar \
-Dpaper.maxChatCommandInputSize=2048 \
"$SERVER_PATH" --nogui
