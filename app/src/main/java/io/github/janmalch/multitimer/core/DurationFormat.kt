package io.github.janmalch.multitimer.core

import kotlin.time.Duration

@JvmInline
value class DurationFormat(private val format: String) {
    init {
        require(format.isNotBlank()) {
            "Duration format cannot be blank, but got \"$format\"."
        }
        for (ch in format) {
            if (!ch.isDigit()) continue
            val digit = ch.digitToInt(10)
            require(digit in 1..5) {
                "Duration format may only include the digits 1, 2, and 3, but got \"$format\"."
            }
        }
        require("1" in format) {
            "Duration format must include '1' as hours placeholder, but got \"$format\"."
        }
        require("23" in format) {
            "Duration format must include '23' as minutes placeholder, but got \"$format\"."
        }
        if ('4' in format || '5' in format) {
            require("45" in format) {
                "Duration format has malformed seconds placeholder. Should be '45' but got \"$format\"."
            }
        }
    }

    fun format(duration: Duration): String = format
        .replace("1", duration.inWholeHours.toString(10))
        .replace("23", (duration.inWholeMinutes % 60).toString(10).padStart(2, '0'))
        .replace("45", (duration.inWholeSeconds % 60).toString(10).padStart(2, '0'))

    companion object {
        fun isValid(format: String): Boolean = runCatching { DurationFormat(format) }.isSuccess
    }
}