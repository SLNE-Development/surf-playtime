import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.standalone")
    id("dev.slne.surf.microservice")
}

dependencies {
    api(projects.surfPlaytimeCore.surfPlaytimeCoreCommon)
    implementation(projects.surfPlaytimeDatabase)
}

surfStandaloneApi {
    withSurfDatabaseR2dbc("2.3.4", "dev.slne.surf.playtime.libs.database")
}

surfMicroservice {
    withMicroserviceApi()
    withRabbitModule(RabbitModule.SERVER_API, true)
}