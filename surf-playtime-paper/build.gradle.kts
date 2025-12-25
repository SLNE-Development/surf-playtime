plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.playtime.paper.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    authors.add("red")
}