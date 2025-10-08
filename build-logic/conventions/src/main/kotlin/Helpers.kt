/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.expand
import org.gradle.kotlin.dsl.getByType
import org.gradle.language.jvm.tasks.ProcessResources

val Project.libs: LibrariesForLibs
    get() = rootProject.extensions.getByType()

fun Project.isRelease(): Boolean {
    return !project.version.toString().contains("-")
}

fun Javadoc.applyLinks(vararg links: String) {
    (options as StandardJavadocDocletOptions).apply {
        links(*links)
    }
}

fun ProcessResources.expandProperties(vararg files: String) {
    // The configuration-cache requires us to use temp variables.
    // Gradle recommends value providers, but those do not currently work with gradle.properties
    // see https://github.com/gradle/gradle/issues/23572
    val d = project.rootProject.description.toString()
    val v = project.rootProject.version.toString()

    inputs.property("description", d)
    inputs.property("version", v)

    filesMatching(files.toList()) {
        expand(
            "description" to d,
            "version" to v,
        )
    }
}

fun JavaCompile.addCompilerArgs() {
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            "-Xlint:-options",
            "-Xlint:deprecation",
            "-Xlint:unchecked"
        )
    )
}

fun Jar.includeLicenseFile() {
    from(project.rootProject.file("LICENSE")) {
        into("META-INF")
    }
}

fun Jar.configureManifest() {
    manifest {
        attributes(
            mapOf(
                "License" to "MPL-2.0",
                "License-URL" to "https://mozilla.org/MPL/2.0/"
            )
        )
    }
}