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
        if (System.getenv("LIVEUPDATESPROVIDER_PUBLISH") == "true") {
            classpath("io.github.gradle-nexus:publish-plugin:2.0.0")
        }

        classpath("com.android.tools.build:gradle:8.13.0")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}

if (System.getenv("LIVEUPDATESPROVIDER_PUBLISH") == "true") {
    apply(plugin = "io.github.gradle-nexus.publish-plugin")
    apply(from = file("./live-updates-provider/scripts/publish-root.gradle"))
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
