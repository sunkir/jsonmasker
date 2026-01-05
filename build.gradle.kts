plugins {
    kotlin("jvm") version "2.2.20"
}

group = "dev.sankir.utils.jsonmasker"

version = System.getenv("GITHUB_REF_NAME")?.removePrefix("v") ?: "1.0.0-SNAPSHOT"

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

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}