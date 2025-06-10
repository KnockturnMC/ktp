version = "1.0.0-SNAPSHOT"
testing.suites.withType<JvmTestSuite> { useJUnitJupiter() }

repositories {
    maven("https://libraries.minecraft.net")
    mavenCentral()
}

dependencies {
    compileOnly(project(":ktp-api"))
    compileOnly(project(":ktp-server"))

    testImplementation(project(":ktp-api"))
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
