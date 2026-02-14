package io.github.janmalch.multitimer.core

import kotlinx.datetime.TimeZone
import kotlin.time.Duration


class Report(
    private val events: List<Event>,
    private val tz: TimeZone = TimeZone.currentSystemDefault()
) {

    fun toTextReport(): String {
        if (events.isEmpty()) return "0 events"

        val sb = StringBuilder()
        if (events.first().isStopEvent) {
            sb.append("ERROR: report cannot start with stop event\n\n")
            sb.appendEventList()
            return sb.toString()
        }

        val sections = mutableListOf<Pair<String, Duration>>()
        var current = events.first().timestamp
        for ((i, event) in events.withIndex()) {
            if (i == 0) continue
            val currentName = events[i - 1].timerName
            if (currentName != null) {
                sections += currentName to (event.timestamp - current)
            }
            current = event.timestamp
        }
        val summaries = sections.groupBy { it.first }.map { entry ->
            entry.key to entry.value.fold(Duration.ZERO) { acc, section -> acc + section.second }
        }.sortedBy { it.first }


        sb.appendLine("${summaries.size} timers:")
        for ((timer, duration) in summaries) {
            sb.appendLine("- $timer for $duration")
        }

        sb.append('\n')
        sb.appendLine("${sections.size} sections:")
        for ((timer, duration) in sections) {
            sb.appendLine("- $timer for $duration")
        }

        sb.append('\n')
        sb.appendEventList()

        return sb.toString()
    }

    private fun StringBuilder.appendEventList() {
        appendLine("${events.size} events:")
        for (event in events) {
            if (event.isStopEvent) {
                appendLine("- ${event.timestamp} > stop")
            } else {
                appendLine("- ${event.timestamp} > switch to ${event.timerName}")
            }
        }
    }

}