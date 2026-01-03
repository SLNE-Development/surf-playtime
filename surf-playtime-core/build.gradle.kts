plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

surfCoreApi {
    withSurfRedis()
}

dependencies {
    api(project(":surf-playtime-api"))
}