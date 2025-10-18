plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation(project(":common"))
    compileOnly("digital.proona:redis-client:1.0")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

kotlin {
    jvmToolchain(21)
}