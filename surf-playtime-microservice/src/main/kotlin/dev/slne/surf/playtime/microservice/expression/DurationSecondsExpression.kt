package dev.slne.surf.playtime.microservice.expression

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Expression
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Function
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.LongColumnType
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.QueryBuilder
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.vendors.MysqlDialect
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.vendors.currentDialect

class DurationSecondsExpression(
    private val start: Expression<*>,
    private val end: Expression<*>,
) : Function<Long>(LongColumnType()) {

    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        when (currentDialect) {
            is PostgreSQLDialect -> {
                append("CAST(TRUNC(EXTRACT(EPOCH FROM (")
                append(end)
                append(" - ")
                append(start)
                append("))) AS BIGINT)")
            }

            is MysqlDialect -> {
                append("TIMESTAMPDIFF(SECOND, ")
                append(start)
                append(", ")
                append(end)
                append(")")
            }

            else -> error(
                "Unsupported database dialect: ${currentDialect::class.qualifiedName}"
            )
        }
    }
}