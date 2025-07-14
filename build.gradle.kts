@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.loom)
}

loom {
    accessWidenerPath = file("src/main/resources/orbit.accesswidener")
}

repositories {
    maven("https://maven.parchmentmc.org") // Parchment Mappings
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") // DevAuth
    maven("https://nexus.resourcefulbees.com/repository/maven-public/") // Olympus
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(libs.parchment)
    })
    modImplementation(libs.loader.fabric)
    modImplementation(libs.loader.kotlin)

    modImplementation(libs.fapi)

    modImplementation(libs.resourcefullib)
    include(libs.resourcefullib)
    modImplementation(libs.olympus)
    modImplementation(libs.modmenu)
    include(libs.olympus)

    modRuntimeOnly(libs.devauth)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}
