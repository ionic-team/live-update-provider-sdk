plugins {
    id("com.android.library") version "8.13.0" apply false
    kotlin("android") version "2.1.0" apply false
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(providers.gradleProperty("ossrhUsername"))
            password.set(providers.gradleProperty("ossrhPassword"))
            stagingProfileId.set(providers.gradleProperty("sonatypeStagingProfileId"))
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
