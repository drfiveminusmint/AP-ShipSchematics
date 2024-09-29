plugins {
    `java-library`
    `maven-publish`
    id("io.github.0ffz.github-packages") version "1.2.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven { githubPackage("apdevteam/movecraft")(this) }
    maven { githubPackage("apdevteam/movecraft-repair")(this) }
    maven { githubPackage("apdevteam/movecraft-worldguard")(this) }
}

dependencies {
    api("org.jetbrains:annotations-java5:24.1.0")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("net.countercraft:movecraft:+")
    compileOnly("net.countercraft.movecraft.repair:movecraft-repair:1.0.0_beta-6")
    compileOnly("net.countercraft.movecraft.worldguard:movecraft-worldguard:+")
    compileOnly("it.unimi.dsi:fastutil:8.5.11")
}

group = "com.github.drfiveminusmint.APShipSchematics"
version = "1.0.0_beta-1"
description = "AP-ShipSchematics"
java.toolchain.languageVersion = JavaLanguageVersion.of(17)

tasks.jar {
    archiveBaseName.set("AP-ShipSchematics")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.processResources {
    from(rootProject.file("LICENSE.md"))
    filesMatching("*.yml") {
        expand(mapOf("projectVersion" to project.version))
    }
}
