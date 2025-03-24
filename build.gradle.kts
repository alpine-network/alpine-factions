import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.jvm.tasks.Jar

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta11"
    id("maven-publish")
}

val mavenGroup: String by project.properties
val mavenArtifact: String by project.properties
val pluginName: String by project.properties
val pluginDescription: String by project.properties
val serverVersion: String by project.properties
val props = mapOf(
    "mavenArtifact" to mavenArtifact,
    "pluginName" to pluginName,
    "pluginDescription" to pluginDescription,
    "pluginGroup" to "${mavenGroup}.${mavenArtifact}",
    "pluginVersion" to version,
)

group = props["pluginGroup"]!!

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

/* Subprojects */
allprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven("https://lib.alpn.cloud/alpine-public/")
        maven("https://lib.alpn.cloud/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.panda-lang.org/releases")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    dependencies {
        compileOnly(group = "com.destroystokyo.paper", name = "paper-api", version = serverVersion)
        compileOnly(group = "co.crystaldev", name = "alpinecore", version = "0.4.10-SNAPSHOT")

        compileOnly(group = "me.clip", name = "placeholderapi", version = "2.11.5")

        compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.36")
        annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.36")
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        withType<ProcessResources> {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            inputs.properties(props)
            filesMatching("plugin.yml") {
                expand(props)
            }
        }

        getByName("compileTestJava") {
            this.enabled = false
        }

        // shadow
        withType<ShadowJar> {
            dependsOn("jar")
            outputs.upToDateWhen { false }

            val suffix = if (project.name == "simple") "Simple" else ""
            archiveFileName.set("$pluginName$suffix-$version.jar")
            archiveClassifier.set("")

            from("${project.layout.projectDirectory}/resources/plugin.yml") {
                into("/")
            }
        }
        withType<Jar> {
            archiveClassifier.set("dev")
        }
        build {
            dependsOn(shadowJar)
        }

        // replaceTokens
        register("replaceTokens") {
            doLast {
                // Define the temporary directory where the files will be copied to
                val tempSrcDir = project.layout.buildDirectory.dir("tempSrc").get().asFile
                if (tempSrcDir.exists())
                    tempSrcDir.deleteRecursively()

                // Copy all Java files from 'src/main/java' to the temporary directory
                copy {
                    from("src/main/java")
                    into(tempSrcDir)
                }

                val javaFiles = project.fileTree(tempSrcDir) {
                    include("**/*.java")
                }

                javaFiles.forEach { file ->
                    var content = file.readText()
                    props.forEach {
                        val token = "{{ ${it.key} }}"
                        if (content.contains(token)) {
                            content = content.replace(token, it.value as String)
                            file.writeText(content)
                        }
                    }
                }
            }
        }
        withType<JavaCompile> {
            dependsOn("replaceTokens")

            // Change the Java compilation source to the modified files in the temp directory
            source = fileTree("${project.layout.buildDirectory.get()}/tempSrc")
        }
    }
}

/* Publishing */
java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set(pluginName)
                description.set(pluginDescription)

                groupId = mavenGroup
                artifactId = mavenArtifact
                version = rootProject.version as String
                packaging = "jar"
            }
        }
    }
    repositories {
        maven {
            name = "AlpineCloud"
            url = uri("https://lib.alpn.cloud/alpine-private")
            credentials {
                username = System.getenv("ALPINE_MAVEN_NAME")
                password = System.getenv("ALPINE_MAVEN_SECRET")
            }
        }
    }
}
