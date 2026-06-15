import org.gradle.kotlin.dsl.withGroovyBuilder
import org.gradle.api.provider.Property
import java.net.URI
import java.util.Properties

extra["signing.keyId"] = ""
extra["signing.key"] = ""
extra["signing.password"] = ""
extra["centralUsername"] = ""
extra["centralPassword"] = ""
extra["sonatypeStagingProfileId"] = ""

val secretPropsFile = file("./local.properties")
if (secretPropsFile.exists()) {
    val properties = Properties()
    secretPropsFile.inputStream().use { properties.load(it) }
    properties.forEach { name, value ->
        extra[name.toString()] = value
    }
} else {
    extra["centralUsername"] = System.getenv("ANDROID_CENTRAL_USERNAME")
    extra["centralPassword"] = System.getenv("ANDROID_CENTRAL_PASSWORD")
    extra["sonatypeStagingProfileId"] = System.getenv("ANDROID_SONATYPE_STAGING_PROFILE_ID")
    extra["signing.keyId"] = System.getenv("ANDROID_SIGNING_KEY_ID")
    extra["signing.key"] = System.getenv("ANDROID_SIGNING_KEY")
    extra["signing.password"] = System.getenv("ANDROID_SIGNING_PASSWORD")
}

extensions.getByName("nexusPublishing").withGroovyBuilder {
    "repositories" {
        "sonatype" {
            setProperty("stagingProfileId", extra["sonatypeStagingProfileId"])
            setProperty("username", extra["centralUsername"])
            setProperty("password", extra["centralPassword"])
            @Suppress("UNCHECKED_CAST")
            (getProperty("nexusUrl") as Property<URI>).set(
                uri("https://ossrh-staging-api.central.sonatype.com/service/local/")
            )
            @Suppress("UNCHECKED_CAST")
            (getProperty("snapshotRepositoryUrl") as Property<URI>).set(
                uri("https://central.sonatype.com/repository/maven-snapshots/")
            )
        }
    }
    setProperty(
        "repositoryDescription",
        "Live Update Provider SDK v${System.getenv("LIVE_UPDATE_PROVIDER_SDK_VERSION")}"
    )
}
