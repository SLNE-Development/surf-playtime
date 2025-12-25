plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

dependencies {
    api(project(":surf-playtime-core"))
    api("dev.slne.surf:surf-database:2.0.4-SNAPSHOT")
}