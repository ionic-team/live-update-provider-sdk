plugins {
    id("org.jetbrains.dokka") version "2.0.0" apply false
    id("com.vanniktech.maven.publish") version "0.30.0" apply false
}

buildscript {
    val kotlinVersion = "2.1.0"
    extra.apply {
        set("kotlinVersion", kotlinVersion)
    }

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
