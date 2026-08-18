import dev.slne.surf.api.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.playtime.paper.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    withCorePaper()

    authors.add("red")

    serverDependencies {
        registerSoft("surf-transaction-paper")
    }
}

dependencies {
    api(projects.surfPlaytimeCore.surfPlaytimeCoreClient)
    api(projects.surfPlaytimeApi.surfPlaytimeApiPaper)
    compileOnly("dev.slne.surf.transaction:surf-transaction-api:+")
}
