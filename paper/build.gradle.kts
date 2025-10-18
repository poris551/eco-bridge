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
    implementation(project(":common"))
    compileOnly("digital.proona:redis-client:1.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
}


kotlin {
    jvmToolchain(21)
}