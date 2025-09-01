plugins {
    id("factions.build-logic")
}

subprojects {
    apply {
        plugin("factions.base-conventions")
    }
}