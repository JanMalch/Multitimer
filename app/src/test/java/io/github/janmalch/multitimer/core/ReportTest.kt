package io.github.janmalch.multitimer.core

import kotlinx.datetime.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Instant

class ReportTest {

    @Test
    fun `should work`() {
        val report = Report(listOf(
            Event("A", Instant.fromEpochSeconds(0)),
            Event("B", Instant.fromEpochSeconds(1)),
            Event("A", Instant.fromEpochSeconds(5)),
            Event(null, Instant.fromEpochSeconds(10)),
        ), TimeZone.UTC)
        assertEquals("""
            2 timers:
            - A for 6s
            - B for 4s
            
            3 sections:
            - A for 1s
            - B for 4s
            - A for 5s
            
            4 events:
            - 1970-01-01T00:00:00Z > switch to A
            - 1970-01-01T00:00:01Z > switch to B
            - 1970-01-01T00:00:05Z > switch to A
            - 1970-01-01T00:00:10Z > stop
            
        """.trimIndent(), report.toTextReport())
    }

    @Test
    fun `should work with break in between`() {
        val report = Report(listOf(
            Event("A", Instant.fromEpochSeconds(0)),
            Event("B", Instant.fromEpochSeconds(1)),
            Event("A", Instant.fromEpochSeconds(5)),
            Event(null, Instant.fromEpochSeconds(10)),
            Event("B", Instant.fromEpochSeconds(15)),
            Event("C", Instant.fromEpochSeconds(25)),
            Event(null, Instant.fromEpochSeconds(35)),
        ), TimeZone.UTC)
        assertEquals("""
            3 timers:
            - A for 6s
            - B for 14s
            - C for 10s
            
            5 sections:
            - A for 1s
            - B for 4s
            - A for 5s
            - B for 10s
            - C for 10s
            
            7 events:
            - 1970-01-01T00:00:00Z > switch to A
            - 1970-01-01T00:00:01Z > switch to B
            - 1970-01-01T00:00:05Z > switch to A
            - 1970-01-01T00:00:10Z > stop
            - 1970-01-01T00:00:15Z > switch to B
            - 1970-01-01T00:00:25Z > switch to C
            - 1970-01-01T00:00:35Z > stop
            
        """.trimIndent(), report.toTextReport())
    }

}