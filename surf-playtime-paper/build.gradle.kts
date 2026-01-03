plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.playtime.paper.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    withCorePaper()
    withSurfRedis()

    authors.add("red")
}

dependencies {
    api(project(":surf-playtime-core"))
    runtimeOnly(project(":surf-playtime-fallback"))
}