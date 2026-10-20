plugins {
    id("dev.slne.surf.api.gradle.core")
}

surfCoreApi {
    withSurfDatabaseR2dbc("2.3.5", "dev.slne.surf.playtime.libs.database")
}

dependencies {
    implementation(projects.surfPlaytimeCore.surfPlaytimeCoreCommon)
}