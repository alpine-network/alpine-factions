dependencies {
    compileOnly("org.bukkit:bukkit:1.12.2-R0.1-SNAPSHOT")
    compileOnly("co.crystaldev:alpinecore:0.4.+")
    compileOnly("co.crystaldev:alpinefactions:0.4.+")
}

tasks {
    jar {
        archiveFileName.set("SimpleAlpineFactions-${project.version}.jar")
    }
    processResources {
        expand("version" to project.version)
    }
}