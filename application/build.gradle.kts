import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktorVersion: String by rootProject
val logbackVersion: String by rootProject
val logbackEncoderVersion: String by rootProject
val koinVersion: String by rootProject
val janinoVersion: String by rootProject
val awsVersion: String by rootProject

application {
    mainClassName = "com.cinema.application.AppKt"
}

plugins {
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":adapters"))
    implementation(project(":domain"))

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-metrics:$ktorVersion")
    implementation("io.ktor:ktor-metrics-micrometer:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("org.koin:koin-logger-slf4j:$koinVersion")
    implementation("org.koin:koin-ktor:$koinVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("ch.qos.logback:logback-core:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")
    implementation("org.codehaus.janino:janino:$janinoVersion")

    // AWS
    implementation(platform("software.amazon.awssdk:bom:$awsVersion"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")
    implementation("software.amazon.awssdk:netty-nio-client:$awsVersion")
}

tasks {

    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("cinema")
        archiveClassifier.set("")
        archiveVersion.set("")
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
    }

    named<JavaExec>("run") {
        doFirst {
            args = listOf("run")
        }
    }
}

tasks.register<Copy>("installGitHook") {
    // binary representation of 777
    val roles = 0b111101101
    from(File(rootProject.rootDir, "hooks/pre-commit"))
    into { File(rootProject.rootDir, ".git/hooks/") }
    fileMode = roles
}

tasks.getByName("build") {
    dependsOn("installGitHook")
}
