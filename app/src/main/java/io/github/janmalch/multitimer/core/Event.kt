package io.github.janmalch.multitimer.core

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Event(
    val timerName: String?,
    val timestamp: Instant,
) {
    val isStopEvent: Boolean get() = timerName == null
}