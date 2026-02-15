package io.github.janmalch.multitimer.ui.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.janmalch.multitimer.core.EventRepository
import io.github.janmalch.multitimer.core.TodaysTimersUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val todaysTimersUseCase: TodaysTimersUseCase,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val _onEventFailed = Channel<Unit>()
    val onEventFailed = _onEventFailed.receiveAsFlow()

    val timers = todaysTimersUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val mostRecentEvent = eventRepository.todaysEvents().map { it.lastOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    fun addEvent(timerName: String?) {
        viewModelScope.launch {
            try {
                eventRepository.addEvent(timerName)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to add event.", e)
                _onEventFailed.send(Unit)
            }
        }
    }

}