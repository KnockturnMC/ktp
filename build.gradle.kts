import io.papermc.paperweight.util.constants.*

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0" apply false
    id("io.papermc.paperweight.patcher") version "1.1.11"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        content { onlyForConfigurations(PAPERCLIP_CONFIG) }
    }
}

dependencies {
    remapper("org.quiltmc:tiny-remapper:0.4.3")
    decompiler("net.minecraftforge:forgeflower:1.5.498.12")
    paperclip("io.papermc:paperclip:2.0.1")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(16))
        }
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(16)
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
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

    usePaperUpstream(providers.gradleProperty("paperRef")) {
        withPaperPatcher {
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("KTP-API"))

            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("KTP-Server"))
        }
    }
}

//
// Everything below here is optional if you don't care about publishing API or dev bundles to your repository
//

tasks.generateDevelopmentBundle {
    apiCoordinates.set("dev.lynxplay.ktp:ktp-api")
    mojangApiCoordinates.set("io.papermc.paper:paper-mojangapi")
    libraryRepositories.set(
        listOf(
            "https://libraries.minecraft.net/",
            "https://maven.quiltmc.org/repository/release/",
            "https://repo.aikar.co/content/groups/aikar",
            "https://ci.emc.gs/nexus/content/groups/aikar/",
            "https://papermc.io/repo/repository/maven-public/", // for paper-mojangapi
            "https://repo.knockturnmc.com/content/repositories/knockturn-public/"
        )
    )
}

allprojects {
    // Publishing API:
    // ./gradlew :KTP-API:publish[ToMavenLocal]
    publishing {
        repositories {
            maven {
                name = "knockturnPublic"
                url = uri("https://repo.knockturnmc.com/content/repositories/knockturn-public/")
                credentials(PasswordCredentials::class)
            }
        }
    }
}

publishing {
    // Publishing dev bundle:
    // ./gradlew publishDevBundlePublicationTo(MavenLocal|MyRepoSnapshotsRepository) -PpublishDevBundle
    if (project.hasProperty("publishDevBundle")) {
        publications.create<MavenPublication>("devBundle") {
            artifact(tasks.generateDevelopmentBundle) {
                artifactId = "dev-bundle"
            }
        }
    }
}
