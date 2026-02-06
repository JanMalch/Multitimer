package io.github.janmalch.multitimer.ui.screens.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.janmalch.multitimer.ui.theme.MultiTimerTheme
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Timer(
        base = Duration.ZERO,
        since = remember { Clock.System.now() },
        isRunning = true,
    )
}

@Composable
private fun Timer(
    base: Duration,
    since: Instant,
    isRunning: Boolean,
    clock: Clock = remember { Clock.System },
) {
    var elapsedMs by rememberSaveable(base) { mutableLongStateOf(base.inWholeMilliseconds) }
    LaunchedEffect(isRunning, since, base) {
        if (!isRunning) {
            elapsedMs = base.inWholeMilliseconds
            return@LaunchedEffect
        }
        while (isRunning) {
            elapsedMs = ((clock.now() - since) + base).inWholeMilliseconds
            delay(50)
        }
    }
    Text(elapsedMs.milliseconds.toString())
}


@Preview(showBackground = true)
@Composable
private fun TimerFromZeroRunningPreview() {
    MultiTimerTheme {
        Timer(
            base = Duration.ZERO,
            since = remember { Clock.System.now() },
            isRunning = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerFromZeroNotRunningPreview() {
    MultiTimerTheme {
        Timer(
            base = Duration.ZERO,
            since = remember { Clock.System.now() },
            isRunning = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerFromOneHourRunningPreview() {
    MultiTimerTheme {
        Timer(
            base = 1.hours,
            since = remember { Clock.System.now() },
            isRunning = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerFromOneHourNotRunningPreview() {
    MultiTimerTheme {
        Timer(
            base = 1.hours,
            since = remember { Clock.System.now() },
            isRunning = false,
        )
    }
}