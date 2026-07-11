package io.github.janmalch.multitimer.core

import io.github.janmalch.multitimer.models.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

data class TodaysTimer(
    val timer: Timer,
    val recorded: Duration
)

data class Today(
    val timers: List<TodaysTimer>,
    val total: Duration,
) {
    companion object {
        fun empty() = Today(emptyList(), Duration.ZERO)
    }
}

class TodaysTimersUseCase(
    private val configRepository: ConfigRepository,
    private val eventRepository: EventRepository,
    private val dispatcher: CoroutineContext,
) {
    @Inject
    constructor(
        configRepository: ConfigRepository,
        eventRepository: EventRepository,
    ) : this(configRepository, eventRepository, Dispatchers.Default)


    operator fun invoke(): Flow<Today> =
        combine(configRepository.timers, eventRepository.todaysEvents()) { timers, events ->
            val report = Report(events)
            var total = Duration.ZERO
            val todaysTimers = timers.map {
                TodaysTimer(
                    timer = it,
                    recorded = report.totalByTimer[it.name]?.also { total += it } ?: Duration.ZERO
                )
            }
            Today(todaysTimers, total)
        }.flowOn(dispatcher)
}