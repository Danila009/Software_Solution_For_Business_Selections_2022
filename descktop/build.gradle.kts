import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
}

group = "me.danbe"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)

    //Ktor
    implementation("io.ktor:ktor-client-core:2.0.2")
    implementation("io.ktor:ktor-client-logging:2.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.2")
    implementation("io.ktor:ktor-client-apache:2.0.2")

    implementation("com.alialbaali.kamel:kamel-image:0.2.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "untitled1"
            packageVersion = "1.0.0"
        }
    }
}