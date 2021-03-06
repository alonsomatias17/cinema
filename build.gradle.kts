import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val koinVersion: String by rootProject
val kotlinVersion: String by rootProject
val ktorServerTestVersion: String by rootProject
val ktorClientTestVersion: String by rootProject
val kotlinTestJunit: String by rootProject
val jUnitJupiterVersion: String by rootProject
val detektVersion: String by rootProject
val kotlinTestVersion: String by rootProject
val mockkVersion: String by rootProject
val koinTestVersion: String by rootProject
val kotlinReactiveVersion: String by rootProject
val kotlinReflectVersion: String by rootProject
val logbackVersion: String by rootProject
val logbackJsonVersion: String by rootProject
val logbackEncoderVersion: String by rootProject
val awsVersion: String by rootProject
val ktorVersion: String by rootProject
val ktorOpenAPIVersion: String by rootProject

plugins {
    jacoco
    base
    kotlin("jvm") version "1.5.30"
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
    id("org.sonarqube") version "3.0"
}

buildscript {
    repositories { jcenter() }

    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.5.30"))
        classpath(kotlin("serialization", version = "1.5.30"))
    }
}

allprojects {
    group = "com.cinema"
    version = "1.0"

    apply(plugin = "kotlin")
    apply(plugin = "jacoco")
    apply(plugin = "io.gitlab.arturbosch.detekt")


    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://jcenter.bintray.com/") }
        maven { url = uri("https://jitpack.io") }
    }
    detekt {
        toolVersion = detektVersion
        input = files("src/main/kotlin", "src/test/kotlin")
        config = files("${rootProject.projectDir}/config/detekt/config.yml")
        baseline = file("${rootProject.projectDir}/config/detekt/baseline.xml")
        autoCorrect = true
        parallel = true
    }
}

subprojects {
    dependencies {
        // Kotlin
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$kotlinReactiveVersion")
        implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinReflectVersion")

        api("ch.qos.logback:logback-classic:$logbackVersion")
        api("ch.qos.logback.contrib:logback-json-classic:$logbackJsonVersion")
        api("ch.qos.logback.contrib:logback-jackson:$logbackJsonVersion")
        api("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

        implementation("org.koin:koin-core:$koinVersion")
        //Ktor
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-apache:$ktorVersion")
        implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
        implementation("io.ktor:ktor-server-core:$ktorVersion")
        implementation("io.ktor:ktor-server-test-host:$ktorServerTestVersion")

        //TODO: add version variable

        testImplementation("org.junit.jupiter:junit-jupiter:$jUnitJupiterVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitJupiterVersion")
        testImplementation("io.kotlintest:kotlintest-assertions:$kotlinTestVersion")
        testImplementation("io.ktor:ktor-server-tests:$ktorServerTestVersion")
        testImplementation("io.ktor:ktor-client-mock:$ktorClientTestVersion")
        testImplementation("io.mockk:mockk:$mockkVersion")

        // AWS
        implementation(platform("software.amazon.awssdk:bom:$awsVersion"))
        implementation("software.amazon.awssdk:dynamodb-enhanced")
        implementation("software.amazon.awssdk:netty-nio-client:$awsVersion")

        //DOC
        implementation("com.github.papsign:Ktor-OpenAPI-Generator:$ktorOpenAPIVersion")

        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitJupiterVersion")
        testImplementation("org.koin:koin-test:$koinTestVersion")

        detekt("io.gitlab.arturbosch.detekt:detekt-cli:$detektVersion")
        detekt("io.gitlab.arturbosch.detekt:detekt-core:$detektVersion")
        detekt("io.gitlab.arturbosch.detekt:detekt-api:$detektVersion")
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
    }
    tasks {

        withType<Detekt> {
            onlyIf { !project.hasProperty("skipDetekt") }
            parallel = true
            buildUponDefaultConfig = false
        }

        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
            kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
        }

        named<Test>("test") {
            systemProperty("ENV", "test")
            useJUnitPlatform()
            testLogging {
                events = setOf(FAILED, PASSED, SKIPPED)
            }
        }

        named("build") {
            dependsOn("test")
        }

        withType<JacocoReport> {
            dependsOn("test")
        }

        withType<Test> {
            dependsOn("detekt")
        }
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "16"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "16"
}


