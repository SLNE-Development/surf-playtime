package dev.slne.surf.playtime.paper.standalone.afk

import java.util.concurrent.atomic.AtomicBoolean

class AfkState(@Volatile var lastActivityNanos: Long) {
    val afk = AtomicBoolean(false)
}
