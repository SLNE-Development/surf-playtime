plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

surfCoreApi {
    withSurfRedis()
}

dependencies {
    api(project(":surf-playtime-core"))
    api("dev.slne.surf:surf-database-r2dbc:1.0.0-SNAPSHOT")
}