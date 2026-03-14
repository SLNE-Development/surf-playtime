import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
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
    api(project(":surf-playtime-core"))
    runtimeOnly(project(":surf-playtime-fallback"))
    compileOnly("dev.slne.surf.transaction:surf-transaction-api:1.21.11-3.0.1")
}