package io.github.janmalch.multitimer.models

import org.junit.Test

import org.junit.Assert.*

class TimerTest {
    /**
     * Test equality for distinctUntilChanged on repository flow.
     */
    @Test
    fun `two instances should equal based on data`() {
        assertEquals(
            Timer.newBuilder().setName("A").build(),
            Timer.newBuilder().setName("A").build(),
        )
    }
}