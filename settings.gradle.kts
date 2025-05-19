plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "ktp"

include("ktp-api", "ktp-server")

val testPlugin = file("test-plugin.settings.gradle.kts")
if (testPlugin.exists()) {
    apply(from = testPlugin)
} else {
    testPlugin.writeText("// Uncomment to enable the test plugin module\n//include(\":test-plugin\")\n")
}
