package io.github.janmalch.multitimer.core

import android.content.Context
import androidx.room.Insert
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.janmalch.multitimer.models.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.collections.orEmpty
import kotlin.time.Clock
import kotlin.time.Instant

class EventRepository @Inject constructor(
    private val dao: EventDao,
    private val clock: Clock,
    private val timeZone: TimeZone,
) {

    fun todaysEvents(): Flow<List<Event>> = dao.todaysEvents(
        today = clock.now().toLocalDateTime(timeZone).date
    )

    suspend fun addEvent(timerName: String?) = dao.addEvent(
        Event(
            timerName = timerName,
            timestamp = clock.now(),
            timeZone = timeZone,
        )
    )
}
