plugins {
    id("maven-publish")
    id("signing")
    id("com.android.library")
    kotlin("android")
}

// Only configure signing when publishing to remote repository
if (System.getenv("LIVEUPDATESPROVIDER_PUBLISH") == "true") {
    apply(from = file("./scripts/publish-module.gradle"))
}

android {
    namespace = "io.ionic.liveupdatesprovider.provider"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.17.0")

    // Coroutines for suspend functions
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.20.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}

// Extract version from environment or use default
val libVersion = System.getenv("LIVEUPDATESPROVIDER_VERSION") ?: "LOCAL-SNAPSHOT"

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                
                groupId = "io.ionic"
                artifactId = "live-updates-provider"
                version = libVersion
                
                pom {
                    name.set("live-updates-provider")
                    description.set("Ionic Live Updates Provider Native Library")
                    url.set("https://github.com/ionic-team/live-updates-provider-sdk")
                    
                    licenses {
                        license {
                            name.set("Ionic Live Updates Provider License")
                            url.set("https://github.com/ionic-team/live-updates-provider-sdk/blob/main/LICENSE.md")
                        }
                    }
                    
                    developers {
                        developer {
                            name.set("Ionic")
                            email.set("hi@ionic.io")
                        }
                    }
                    
                    scm {
                        connection.set("scm:git:github.com:ionic-team/live-updates-provider-sdk.git")
                        developerConnection.set("scm:git:ssh://github.com:ionic-team/live-updates-provider-sdk.git")
                        url.set("https://github.com/ionic-team/live-updates-provider-sdk/tree/main")
                    }
                }
            }
        }
    }
}
