pluginManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://kotlin.bintray.com/kotlin-eap") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}

rootProject.name = "cinema"
include("domain", "adapters", "application")
