package dev.slne.surf.playtime.paper

import dev.slne.surf.redis.RedisApi

val redisLoader = PaperRedisLoader()
val redisApi get() = redisLoader.redisApi

class PaperRedisLoader {
    lateinit var redisApi: RedisApi

    fun connect() {
        redisApi = RedisApi.create(plugin.dataPath)
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}