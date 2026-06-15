plugins {
    id("org.jetbrains.dokka") version "2.0.0"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0" apply false
}

buildscript {
    val kotlinVersion = "2.1.0"
    extra.apply {
        set("kotlinVersion", kotlinVersion)
    }

    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}

if (System.getenv("LIVE_UPDATE_PROVIDER_PUBLISH") == "true") {
    apply(plugin = "io.github.gradle-nexus.publish-plugin")
    apply(from = file("./live-update-provider/scripts/publish-root.gradle.kts"))
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.dokka")
}
