plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.playtime.paper.standalone.StandalonePaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    withSurfDatabaseR2dbc("2.3.4", "dev.slne.surf.playtime.libs.database")

    authors.add("red")
}

dependencies {
    api(projects.surfPlaytimeApi.surfPlaytimeApiPaper)
    implementation(projects.surfPlaytimeDatabase)
}
