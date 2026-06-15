import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension

apply(plugin = "maven-publish")
apply(plugin = "signing")

val libVersion = requireNotNull(System.getenv("LIVE_UPDATE_PROVIDER_SDK_VERSION")) {
    "LIVE_UPDATE_PROVIDER_SDK_VERSION is required for publishing."
}

group = "io.ionic"
version = libVersion

afterEvaluate {
    extensions.configure<PublishingExtension>("publishing") {
        publications {
            create<MavenPublication>("release") {
                groupId = "io.ionic"
                artifactId = "liveupdateprovider"
                version = libVersion

                from(components["release"])

                pom {
                    name.set("Live Update Provider SDK")
                    description.set("Native contracts for live update providers.")
                    url.set("https://github.com/ionic-team/live-update-provider-sdk")

                    licenses {
                        license {
                            name.set("MIT License")
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

    extensions.configure<SigningExtension>("signing") {
        useInMemoryPgpKeys(
            rootProject.extra["signing.keyId"] as String?,
            rootProject.extra["signing.key"] as String?,
            rootProject.extra["signing.password"] as String?,
        )
        sign(extensions.getByType(PublishingExtension::class.java).publications)
    }
}
