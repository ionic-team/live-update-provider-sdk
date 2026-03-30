plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    id("signing")
}

android {
    namespace = "io.ionic.liveupdateprovider"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

group = "io.ionic"
version = (findProperty("releaseVersion") as String?) ?: "LOCAL-SNAPSHOT"

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                artifactId = "live-update-provider"

                pom {
                    name.set("live-update-provider")
                    description.set("Ionic Live Updates Provider Native Library")
                    url.set("https://github.com/ionic-team/live-update-provider-sdk")

                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://github.com/ionic-team/live-update-provider-sdk/blob/main/License")
                        }
                    }

                    developers {
                        developer {
                            name.set("Ionic")
                            email.set("hi@ionic.io")
                        }
                    }

                    scm {
                        connection.set("scm:git:github.com:ionic-team/live-update-provider-sdk.git")
                        developerConnection.set("scm:git:ssh://github.com:ionic-team/live-update-provider-sdk.git")
                        url.set("https://github.com/ionic-team/live-update-provider-sdk/tree/main")
                    }
                }
            }
        }
    }
}

signing {
    val signingKey = findProperty("signingKey") as String?
    val signingPassword = findProperty("signingPassword") as String?

    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    testImplementation("junit:junit:4.13.2")
}
