plugins {
    id("factions.blossom-conventions")
    id("factions.maven-conventions")
    id("factions.spotless-conventions")
}

dependencies {
    compileOnly(libs.alpinecore)
    compileOnly(libs.bukkit)
    compileOnly(libs.placeholderapi)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("id", project.name)
                property("version", project.version.toString())
            }
        }
    }
}

tasks {
    named("build") {
        dependsOn("javadoc")
    }
    withType<Jar>().configureEach {
        archiveBaseName.set(rootProject.name)
        includeLicenseFile()
    }
    withType<ProcessResources>().configureEach {
        expandProperties("plugin.yml")
    }
    withType<Javadoc>().configureEach {
        enabled = true
        val v = libs.versions
        val alpinecoreVersion = v.alpinecore.get()
        val alpinecoreRepo = if (alpinecoreVersion.contains("-")) "snapshots" else "releases"
        applyLinks(
            "https://docs.oracle.com/en/java/javase/11/docs/api/",
            "https://hub.spigotmc.org/javadocs/spigot/",
            "https://lib.alpn.cloud/javadoc/${alpinecoreRepo}/co/crystaldev/alpinecore/${alpinecoreVersion}/raw/",
            "https://jd.advntr.dev/api/${v.adventure.get()}",
            "https://javadoc.io/doc/org.jetbrains/annotations/${v.annotations.get()}/",
            "https://javadoc.io/doc/com.google.code.gson/gson/2.8.0/",
        )
    }
}