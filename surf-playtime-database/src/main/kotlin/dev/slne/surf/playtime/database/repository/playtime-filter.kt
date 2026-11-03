package dev.slne.surf.playtime.database.repository

import dev.slne.surf.playtime.api.common.session.PlaytimeFilter

/**
 * Whether one of the restrictions of this filter is an empty set, so no session can match.
 */
internal fun PlaytimeFilter.matchesNothing() =
    servers?.isEmpty() == true || categories?.isEmpty() == true || players?.isEmpty() == true
