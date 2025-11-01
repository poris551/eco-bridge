plugins {
    kotlin("jvm") version "2.2.20"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("digital.proona:infinispan-client:1.0")
    compileOnly("digital.proona:rune-lib:1.0")
}

kotlin {
    jvmToolchain(21)
}
