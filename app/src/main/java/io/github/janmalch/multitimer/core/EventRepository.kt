package io.github.janmalch.multitimer.core

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Clock

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
