plugins {
    id("org.jetbrains.dokka") version "2.0.0"
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
        if (System.getenv("LIVE_UPDATE_PROVIDER_PUBLISH") == "true") {
            classpath("io.github.gradle-nexus:publish-plugin:2.0.0")
        }

        classpath("org.jetbrains.dokka:dokka-base:1.9.20")
        classpath("com.android.tools.build:gradle:8.13.0")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))
    }
}

if (System.getenv("LIVE_UPDATE_PROVIDER_PUBLISH") == "true") {
    apply(plugin = "io.github.gradle-nexus.publish-plugin")
    apply(from = file("./live-update-provider/scripts/publish-root.gradle"))
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.dokka")
}

// register Clean task
tasks.register("clean").configure {
    delete("build")
}
