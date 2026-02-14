package io.github.janmalch.multitimer.ui.screens.config

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.janmalch.multitimer.core.ConfigRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
) : ViewModel() {

    private val _onTimerFailed = Channel<Unit>()
    val onTimerFailed = _onTimerFailed.receiveAsFlow()

    val timers = configRepository.timers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun addTimer(name: String, color: String) {
        viewModelScope.launch {
            try {
                configRepository.addTimer(name, color)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("ConfigViewModel", "Failed to add timer.", e)
                _onTimerFailed.send(Unit)
            }
        }
    }

}