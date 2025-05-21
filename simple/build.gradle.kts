plugins {
    id("java")
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":"))
    "shadow"(project(":"))
}
