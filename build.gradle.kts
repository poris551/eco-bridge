import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.2.20"
    id("com.gradleup.shadow") version "9.2.1"
}

repositories {
    mavenCentral()
    mavenLocal()
}

group = "digital.proona"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":common"))
    implementation(project(":velocity"))
    implementation(project(":paper"))
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("eco-bridge")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")
    dependencies {
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib.*"))
        exclude(dependency("org.jetbrains:annotations.*"))
        exclude(dependency("org.intellij:annotations.*"))
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.build {
    dependsOn("shadowJar")
}
