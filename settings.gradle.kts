@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://lib.alpn.cloud/releases/")
        maven("https://lib.alpn.cloud/snapshots/")
        maven("https://lib.alpn.cloud/mirrors/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/releases/")
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

rootProject.name = "alpinefactions"


"factions".also {
    include(it)
    project(":$it").name = "${rootProject.name}-bukkit"
}

include("simple")
