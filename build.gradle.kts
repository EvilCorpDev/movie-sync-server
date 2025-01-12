plugins {
    kotlin("jvm") version "1.9.10"
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val amazonSQSVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:quarkus-amazon-services-bom:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-scheduler:3.17.6")
    implementation("io.quarkiverse.amazonservices:quarkus-amazon-sqs:${amazonSQSVersion}")
    implementation("software.amazon.awssdk:url-connection-client:2.29.49")
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

group = "com.androidghost77"
version = "1.0.0-SNAPSHOT"

kotlin {
    jvmToolchain(17)
}
