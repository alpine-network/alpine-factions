plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

dependencies {
    implementation(project(":"))
    "shadow"(project(":"))
}
