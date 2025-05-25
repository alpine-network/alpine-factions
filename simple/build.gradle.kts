plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.blossom)
}

val pluginName: String by rootProject.properties

dependencies {
    implementation(rootProject)
    compileOnly(libs.bukkit)
}

sourceSets {
    main {
        blossom {
            resources {
                property("version", project.version.toString())
            }
        }
    }
}

tasks.shadowJar {
    archiveBaseName.set("Simple${pluginName}")
    archiveClassifier.set("")

    dependencies {
        include(dependency(rootProject))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    enabled = false
}
