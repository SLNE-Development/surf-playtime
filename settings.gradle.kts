pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.surfapi.gradle.settings") version "1.21.11+"
}

include("surf-playtime-api:surf-playtime-api-common")
include("surf-playtime-api:surf-playtime-api-paper")
include("surf-playtime-core:surf-playtime-core-common")
include("surf-playtime-core:surf-playtime-core-paper")
include("surf-playtime-microservice")
include("surf-playtime-paper")