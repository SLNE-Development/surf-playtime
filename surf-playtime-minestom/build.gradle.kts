import dev.slne.surf.api.gradle.util.slneReleases
import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.minestom")
    id("dev.slne.surf.microservice")
}

surfMinestomApi {
    withCoreMinestom()
}

surfMicroservice {
    withRabbitModule(RabbitModule.CLIENT_API)
}

minestomPluginFile {
    id = "surf-playtime-minestom"
    main = "dev.slne.surf.playtime.minestom.PlaytimeMinestomPlugin"
    authors = listOf("red")

    pluginDependencies {
        register("surf-rabbitmq-minestom")
    }
}

dependencies {
    api(projects.surfPlaytimeCore.surfPlaytimeCoreClient)
    api(projects.surfPlaytimeApi.surfPlaytimeApiMinestom)
    compileOnly("dev.slne.surf.transaction:surf-transaction-api:+")
}

publishing {
    repositories {
        slneReleases()
    }
}
