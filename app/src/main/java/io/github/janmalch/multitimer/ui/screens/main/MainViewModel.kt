package io.github.janmalch.multitimer.ui.screens.main

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.janmalch.multitimer.core.EventRepository
import io.github.janmalch.multitimer.core.Report
import io.github.janmalch.multitimer.core.Today
import io.github.janmalch.multitimer.core.TodaysTimersUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    todaysTimersUseCase: TodaysTimersUseCase,
    private val eventRepository: EventRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val ioDispatcher: CoroutineContext = Dispatchers.IO

    private val _onEventFailed = Channel<Unit>()
    val onEventFailed = _onEventFailed.receiveAsFlow()

    private val _onShareFailed = Channel<Unit>()
    val onShareFailed = _onShareFailed.receiveAsFlow()

    private val _onShareIntent = Channel<Intent>()
    val onShareIntent = _onShareIntent.receiveAsFlow()

    val timers = todaysTimersUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Today.empty(),
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

    fun shareReport() {
        viewModelScope.launch(ioDispatcher) {
            try {
                val report = Report(eventRepository.todaysEvents().first())
                val content = report.toTextReport()
                val file = context.filesDir.resolve("report.txt").apply {
                    writeText(content)
                }

                val uri = FileProvider.getUriForFile(
                    context,
                    "io.github.janmalch.multitimer.fileprovider",
                    file
                )
                val intent = Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra("EXTRA_FILE_PATH", file)
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    },
                    // FIXME: resources
                    "Export report"
                )
                _onShareIntent.send(intent)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to share report.", e)
                _onShareFailed.send(Unit)
            }
        }
    }

}