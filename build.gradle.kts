import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

group = "org.hera"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(
        group = "com.microsoft.cognitiveservices.speech",
        name = "client-sdk",
        version = "1.19.0",
        ext = "jar"
    )
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation(
        group = "org.apache.poi",
        name = "poi",
        version = "5.2.0"
    )
    implementation(
        group = "org.apache.poi",
        name = "poi-ooxml",
        version = "5.2.0"
    )
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

buildConfig {
    buildConfigField("String", "SPEECH_API_KEY", "\"${getSpeechApiKey()}\"")
    buildConfigField("String", "APP_VERSION", "\"${version}\"")
    buildConfigField("Boolean", "SPEECH_ENABLED", "${getBooleanProperty("SPEECH_ENABLED", true)}")
}

fun getSpeechApiKey(): String {
    val value = System.getenv("SPEECH_API_KEY")
    return if (value == null) {
        val keysFile = file("keys.properties")
        val keysProperties = Properties()
        keysProperties.load(FileInputStream(keysFile))
        keysProperties.getProperty("SPEECH_API_KEY")
    } else {
        value
    }
}

fun getBooleanProperty(key: String, defaultValue: Boolean): Boolean {
    return try {
        val keysFile = file("keys.properties")
        val keysProperties = Properties()
        keysProperties.load(FileInputStream(keysFile))
        keysProperties.getProperty(key)?.let { it.toBoolean() } ?: defaultValue
    } catch (e: Exception) {
        defaultValue
    }
}