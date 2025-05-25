import net.kyori.blossom.TemplateSet

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.idea)
    alias(libs.plugins.blossom)
}

val artifactName: String by project.properties
val pluginName: String by project.properties
val pluginDescription: String by project.properties

allprojects {
    group = "co.crystaldev"
    version = "0.4.11"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://lib.alpn.cloud/alpine-public/")
    }
}

repositories {
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    api(libs.alpinecore)

    compileOnly(libs.bukkit)
    compileOnly(libs.placeholderapi)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

sourceSets {
    main {
        blossom {
            javaSources {
                configureReplacements(this)
            }
            resources {
                configureReplacements(this)
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = artifactName
            from(components["java"])

            pom {
                name.set(pluginName)
                description.set(pluginDescription)

                licenses {
                    license {
                        name = "MPL-2.0"
                        url = "https://www.mozilla.org/en-US/MPL/2.0/"
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "Alpine"
            url = uri("https://lib.alpn.cloud/alpine-public/")
            credentials {
                username = System.getenv("ALPINE_MAVEN_NAME")
                password = System.getenv("ALPINE_MAVEN_SECRET")
            }
        }
    }
}

tasks.withType<Jar>().configureEach {
    archiveBaseName.set(pluginName)
}

fun configureReplacements(set: TemplateSet) {
    set.property("artifactName", artifactName)
    set.property("pluginName", pluginName)
    set.property("pluginDescription", pluginDescription)
    set.property("version", project.version.toString())
}
