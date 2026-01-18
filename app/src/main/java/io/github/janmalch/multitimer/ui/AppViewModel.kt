package io.github.janmalch.multitimer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.janmalch.multitimer.core.ConfigRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration


sealed interface AppUiState {
    data object Initializing : AppUiState
    data object Onboarding : AppUiState
    data class Ready(val interval: Duration) : AppUiState
}


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: ConfigRepository,
) : ViewModel() {

    val appUiState = repository.timers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AppUiState.Initializing,
        )


}