plugins {
    `java-library`
    kotlin("jvm") version "2.2.20"
    `maven-publish`
    id("org.jreleaser") version "1.22.0"
}

group = "io.github.sunkir"
version = projectVersion()

configure<PublishingExtension> {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set(project.name)
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
                        name.set("sunkir")
                        url.set("https://github.com/sunkir")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/sunkir/jsonmasker.git")
                    developerConnection.set("scm:git:ssh://github.com/sunkir/jsonmasker.git")
                    url.set("https://github.com/sunkir/jsonmasker")
                }
                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/sunkir/jsonmasker/issues")
                }
            }
        }
    }
}

    repositories {
        mavenCentral()
    }

dependencies {
    val jacksonVersion: String by project
    val assertjVersion: String by project
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
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

jreleaser {
    configFile.set(file("jreleaser.yml"))
}

tasks.test {
    useJUnitPlatform()
}

fun projectVersion() =
    project.findProperty("version")?.toString()?.takeIf { it != "unspecified" }
        ?: try {
            val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0")
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
            process.inputStream.bufferedReader().readText().trim().removePrefix("v")
        } catch (_: Exception) {
            System.getenv("GITHUB_REF_NAME")?.removePrefix("v") ?: "1.0.0"
        }