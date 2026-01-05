import org.gradle.plugins.signing.SigningExtension

plugins {
    kotlin("jvm") version "2.2.20"
    `maven-publish`
    signing
}

group = "dev.sunkir.utils"

version = try {
    val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0")
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
    process.inputStream.bufferedReader().readText().trim().removePrefix("v")
} catch (_: Exception) {
    System.getenv("GITHUB_REF_NAME")?.removePrefix("v") ?: "1.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    val jacksonVersion: String by project
    val assertjVersion: String by project
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation(kotlin("reflect"))

    //testImplementation
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:$assertjVersion")
}

kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("JSON Masker")
                description.set("A simple Kotlin library for masking sensitive data in JSON objects using Jackson.")
                url.set("https://github.com/sunkir/jsonmasker")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("sunkir")
                        name.set("Kirill")
                        email.set("dev.suhachevk.ru")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/sunkir/jsonmasker.git")
                    developerConnection.set("scm:git:ssh://github.com/sunkir/jsonmasker.git")
                    url.set("https://github.com/sunkir/jsonmasker")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://central.sonatype.com/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://central.sonatype.com/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

extensions.configure<SigningExtension> {
    val signingKey = System.getenv("GPG_PRIVATE_KEY")
    val signingPassword = System.getenv("GPG_PASSPHRASE")
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    }
}

tasks.test {
    useJUnitPlatform()
}