pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}

include("surf-playtime-api:surf-playtime-api-common")
include("surf-playtime-api:surf-playtime-api-paper")
include("surf-playtime-api:surf-playtime-api-minestom")
include("surf-playtime-core:surf-playtime-core-common")
include("surf-playtime-core:surf-playtime-core-client")
include("surf-playtime-microservice")
include("surf-playtime-paper")
include("surf-playtime-minestom")
