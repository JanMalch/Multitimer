package io.github.janmalch.multitimer.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.janmalch.multitimer.R
import io.github.janmalch.multitimer.core.DurationFormat
import io.github.janmalch.multitimer.core.Event
import io.github.janmalch.multitimer.models.Timer
import io.github.janmalch.multitimer.ui.components.TimerColorBox
import io.github.janmalch.multitimer.ui.theme.MultiTimerTheme
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

data object MainScreen

@Composable
fun MainScreen(
    goToConfigScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val mostRecentEvent by viewModel.mostRecentEvent.collectAsStateWithLifecycle()
    val timers by viewModel.timers.collectAsStateWithLifecycle()
    MainScreen(
        mostRecentEvent = mostRecentEvent,
        timers = timers,
        goToConfigScreen = goToConfigScreen,
        onStopClick = { viewModel.addEvent(null) },
        onTimerClick = { viewModel.addEvent(it.name) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    mostRecentEvent: Event?,
    timers: List<Timer>,
    goToConfigScreen: () -> Unit,
    onStopClick: () -> Unit,
    onTimerClick: (Timer) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(
                        onClick = goToConfigScreen
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Go to settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            Box(Modifier.padding(horizontal = 24.dp)) {
                Button(
                    onClick = onStopClick,
                    enabled = mostRecentEvent?.timerName != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Stop",
                    )
                }
            }

            TimersColumn(
                mostRecentEvent = mostRecentEvent,
                timers = timers,
                onTimerClick = onTimerClick,
                modifier = Modifier.padding(vertical = 24.dp),
            )
        }
    }
}


@Composable
private fun TimersColumn(
    mostRecentEvent: Event?,
    timers: List<Timer>,
    onTimerClick: (Timer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        timers.forEachIndexed { i, timer ->
            val shape = RoundedCornerShape(
                topStart = if (i == 0) 16.dp else 4.dp,
                topEnd = if (i == 0) 16.dp else 4.dp,
                bottomEnd = if (i == timers.lastIndex) 16.dp else 4.dp,
                bottomStart = if (i == timers.lastIndex) 16.dp else 4.dp,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .clickable(onClick = { onTimerClick(timer) })
                    .background(MaterialTheme.colorScheme.surface, shape)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            ) {
                val isRunning = mostRecentEvent?.timerName == timer.name
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    TimerColorBox(timer)
                    Text(
                        text = timer.name,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold.takeIf { isRunning },
                    )
                }
                TimerText(
                    base = Duration.ZERO,
                    since = remember { Clock.System.now() },
                    isRunning = isRunning,
                )
            }
            Spacer(Modifier.height(1.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimersColumnPreview() {
    MultiTimerTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceDim) {
            TimersColumn(
                mostRecentEvent = null,
                timers = listOf(
                    Timer.newBuilder().setName("A").setColor("#FF0000").build(),
                    Timer.newBuilder().setName("B").setColor("#00FF00").build(),
                    Timer.newBuilder().setName("C").setColor("#0000FF").build(),
                ),
                onTimerClick = {},
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}

private fun durationSaver(): Saver<Duration, Long> = Saver(
    save = { it.inWholeMilliseconds },
    restore = { it.milliseconds }
)

@Composable
private fun TimerText(
    base: Duration,
    since: Instant,
    isRunning: Boolean,
    clock: Clock = remember { Clock.System },
    format: DurationFormat = remember { DurationFormat("1:23:45") }
) {
    var elapsed by rememberSaveable(base, stateSaver = durationSaver()) { mutableStateOf(base) }
    LaunchedEffect(isRunning, since, base) {
        if (!isRunning) {
            elapsed = base
            return@LaunchedEffect
        }
        while (isRunning) {
            elapsed = ((clock.now() - since) + base)
            delay(200)
        }
    }
    Text(format.format(elapsed))
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    var mostRecentEvent by remember { mutableStateOf<Event?>(null) }
    MultiTimerTheme {
        MainScreen(
            mostRecentEvent = mostRecentEvent,
            timers = listOf(
                Timer.newBuilder().setName("A").setColor("#FF0000").build(),
                Timer.newBuilder().setName("B").setColor("#00FF00").build(),
                Timer.newBuilder().setName("C").setColor("#0000FF").build(),
            ),
            onStopClick = {
                mostRecentEvent = Event(timerName = null, timestamp = Clock.System.now())
            },
            goToConfigScreen = {},
            onTimerClick = { timer ->
                mostRecentEvent = Event(
                    timerName = timer.name.takeUnless { it == mostRecentEvent?.timerName },
                    timestamp = Clock.System.now(),
                )
            },
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerTextFromZeroRunningPreview() {
    MultiTimerTheme {
        TimerText(
            base = Duration.ZERO,
            since = remember { Clock.System.now() },
            isRunning = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerTextFromZeroNotRunningPreview() {
    MultiTimerTheme {
        TimerText(
            base = Duration.ZERO,
            since = remember { Clock.System.now() },
            isRunning = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerTextFromOneHourRunningPreview() {
    MultiTimerTheme {
        TimerText(
            base = 1.hours,
            since = remember { Clock.System.now() },
            isRunning = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerTextFromOneHourNotRunningPreview() {
    MultiTimerTheme {
        TimerText(
            base = 1.hours,
            since = remember { Clock.System.now() },
            isRunning = false,
        )
    }
}