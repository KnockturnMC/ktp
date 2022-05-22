version = "1.0.0-SNAPSHOT"

repositories {
    maven("https://libraries.minecraft.net")
    mavenCentral()
}

dependencies {
    compileOnly(project(":ktp-api"))
    compileOnly("io.papermc.paper:paper-mojangapi:1.17.1-R0.1-SNAPSHOT") {
        exclude("io.papermc.paper", "paper-api")
    }

    testImplementation(project(":ktp-api"))
    testImplementation("io.papermc.paper:paper-mojangapi:1.17.1-R0.1-SNAPSHOT") {
        exclude("io.papermc.paper", "paper-api")
    }
    testImplementation(platform("org.junit:junit-bom:5.8.1"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events.addAll(listOf("PASSED", "SKIPPED", "FAILED").map { org.gradle.api.tasks.testing.logging.TestLogEvent.valueOf(it) })
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
