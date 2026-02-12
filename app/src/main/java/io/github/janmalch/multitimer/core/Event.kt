package io.github.janmalch.multitimer.core

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// Modeling isn't exactly perfect, but good enough.
@OptIn(ExperimentalTime::class)
@Entity
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val timerName: String?,
    val timestamp: Instant,
    val date: LocalDate,
) {
    constructor(
        timerName: String?,
        timestamp: Instant,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): this(
        id = 0L,
        timerName = timerName,
        timestamp = timestamp,
        date = timestamp.toLocalDateTime(timeZone).date,
    )

    val isStopEvent: Boolean get() = timerName == null
}

@Dao
interface EventDao {
    @Query("SELECT * FROM Event WHERE date = :today")
    fun todaysEvents(today: LocalDate): Flow<List<Event>>

    @Insert
    suspend fun addEvent(event: Event)
}
