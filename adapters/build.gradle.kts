val ktorVersion: String by rootProject
val koinVersion: String by rootProject
val awsVersion: String by rootProject


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

    // AWS
    implementation(platform("software.amazon.awssdk:bom:$awsVersion"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")
    implementation("software.amazon.awssdk:netty-nio-client:$awsVersion")

    testImplementation((project(":domain").dependencyProject.sourceSets.getAt("test").output))
}
