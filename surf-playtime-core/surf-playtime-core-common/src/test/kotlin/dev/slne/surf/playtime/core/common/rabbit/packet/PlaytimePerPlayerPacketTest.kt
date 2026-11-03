package dev.slne.surf.playtime.core.common.rabbit.packet

import dev.slne.surf.playtime.api.common.session.PlaytimeFilter
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadPlaytimePerPlayerRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.response.PlaytimePerPlayerResponsePacket
import dev.slne.surf.rabbitmq.api.InternalRabbitMQ
import dev.slne.surf.rabbitmq.api.RabbitMQApi
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.EmptySerializersModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

@OptIn(InternalRabbitMQ::class, ExperimentalSerializationApi::class)
class PlaytimePerPlayerPacketTest {
    private val cbor = RabbitMQApi.createCbor(EmptySerializersModule())

    private fun <T> roundTrip(serializer: KSerializer<T>, value: T): T =
        cbor.decodeFromByteArray(serializer, cbor.encodeToByteArray(serializer, value))

    @Test
    fun `request with restrictions survives a round trip`() {
        val filter = PlaytimeFilter(
            from = LocalDateTime.parse("2026-11-02T00:00:00"),
            to = LocalDateTime.parse("2026-11-03T00:00:00"),
            servers = setOf("Survival Events"),
            categories = emptySet(),
            players = setOf(UUID.randomUUID(), UUID.randomUUID())
        )

        val decoded = roundTrip(
            LoadPlaytimePerPlayerRequestPacket.serializer(),
            LoadPlaytimePerPlayerRequestPacket(filter)
        )

        assertEquals(filter, decoded.filter)
    }

    @Test
    fun `request without restrictions survives a round trip`() {
        val filter = PlaytimeFilter(
            from = LocalDateTime.parse("2026-11-02T00:00:00"),
            to = LocalDateTime.parse("2026-11-03T00:00:00")
        )

        val decoded = roundTrip(
            LoadPlaytimePerPlayerRequestPacket.serializer(),
            LoadPlaytimePerPlayerRequestPacket(filter)
        )

        assertEquals(filter, decoded.filter)
    }

    @Test
    fun `response built from a fastutil map survives a round trip`() {
        val playtime = Object2LongOpenHashMap<UUID>().apply {
            put(UUID.randomUUID(), 3600)
            put(UUID.randomUUID(), 61)
        }

        val decoded = roundTrip(
            PlaytimePerPlayerResponsePacket.serializer(),
            PlaytimePerPlayerResponsePacket(playtime)
        )

        assertEquals(HashMap(playtime), HashMap(decoded.playtime))
    }
}
