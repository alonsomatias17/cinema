val ktorVersion: String by rootProject
val koinVersion: String by rootProject

// TODO: delete
plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":domain"))

    //ktor
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    //koin
    implementation("org.koin:koin-core:$koinVersion")
    implementation("org.koin:koin-logger-slf4j:$koinVersion")
    implementation("org.koin:koin-ktor:$koinVersion")

    testImplementation((project(":domain").dependencyProject.sourceSets.getAt("test").output))
}
