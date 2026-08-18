import dev.slne.surf.api.gradle.util.slneReleases

plugins {
    id("dev.slne.surf.api.gradle.minestom")
}

dependencies {
    api(projects.surfPlaytimeApi.surfPlaytimeApiCommon)
}

publishing {
    repositories {
        slneReleases()
    }
}
