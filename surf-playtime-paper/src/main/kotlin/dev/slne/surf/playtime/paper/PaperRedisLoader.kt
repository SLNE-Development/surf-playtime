package dev.slne.surf.playtime.paper

import dev.slne.surf.redis.RedisApi
import java.util.*

val redisLoader = PaperRedisLoader()
val redisApi get() = redisLoader.redisApi

class PaperRedisLoader {
    lateinit var redisApi: RedisApi

    fun connect() {
        redisApi = RedisApi.create()
        plugin.afkPlayers = redisApi.createSyncSet<UUID>("surf-playtime:afk-players")
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}