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

exec /usr/lib/jvm/bin/java -Xms"$SERVER_MEMORY" -Xmx"$SERVER_MEMORY" -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 \
-XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 \
-XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 \
-XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Daikars.new.flags=true -XX:G1NewSizePercent=30 \
-XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M "${EXTRA_ARGS[@]}" "${DEBUG_ARG[@]}" -XX:G1ReservePercent=20 -jar \
-Dpaper.maxChatCommandInputSize=2048 \
/bin/server.jar --nogui
