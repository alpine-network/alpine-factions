/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
plugins {
    idea
    `java-library`
}

plugins.withId("java") {
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

tasks {
    // Target Java 8
    withType<JavaCompile>().configureEach {
        options.release.set(8)
        options.encoding = Charsets.UTF_8.name()
        addCompilerArgs()
    }
    withType<Jar>().configureEach {
        configureManifest()
    }
    withType<ProcessResources>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.WARN
        filteringCharset = Charsets.UTF_8.name()
    }
    withType<Javadoc>().configureEach {
        enabled = false
        configureOptions()
    }
}

fun Javadoc.configureOptions() {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:all,-missing", "-quiet")
        charset(Charsets.UTF_8.name())
        encoding(Charsets.UTF_8.name())
        noTimestamp()
        use()
    }
}