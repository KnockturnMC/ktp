import io.papermc.paperweight.util.constants.*

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0" apply false
    id("io.papermc.paperweight.patcher") version "1.1.11"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        content { onlyForConfigurations(PAPERCLIP_CONFIG) }
    }
    maven("https://maven.quiltmc.org/repository/release/") {
        content { onlyForConfigurations(REMAPPER_CONFIG) }
    }
}

dependencies {
    remapper("org.quiltmc:tiny-remapper:0.4.3")
    decompiler("net.minecraftforge:forgeflower:1.5.498.12")
    paperclip("io.papermc:paperclip:2.0.1")
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(16))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(16)
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://ci.emc.gs/nexus/content/groups/aikar/")
        maven("https://repo.aikar.co/content/groups/aikar")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    }
}

paperweight {
    serverProject.set(project(":KTP-Server"))

    remapRepo.set("https://maven.quiltmc.org/repository/release/")
    decompileRepo.set("https://files.minecraftforge.net/maven/")

    usePaperUpstream(provider { file("current-paper").readText().trim() }) {
        withPaperPatcher {
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("KTP-API"))

            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("KTP-Server"))
        }
    }
}

tasks.paperclipJar {
    destinationDirectory.set(rootProject.layout.projectDirectory)
    archiveFileName.set("ktp-paperclip.jar")
}
