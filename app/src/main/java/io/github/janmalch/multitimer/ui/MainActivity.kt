package io.github.janmalch.multitimer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.github.janmalch.multitimer.ui.screens.config.ConfigScreen
import io.github.janmalch.multitimer.ui.screens.main.MainScreen
import io.github.janmalch.multitimer.ui.theme.MultiTimerTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val backStack = remember { mutableStateListOf<Any>(MainScreen) }
            MultiTimerTheme {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },

                    entryDecorators = listOf(
                        // Add the default decorators for managing scenes and saving state
                        rememberSaveableStateHolderNavEntryDecorator(),
                        // Then add the view model store decorator
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = { key ->
                        when (key) {
                            is MainScreen -> NavEntry(key) {
                                MainScreen(
                                    goToConfigScreen = {
                                        backStack.add(ConfigScreen)
                                    }
                                )
                            }

                            is ConfigScreen -> NavEntry(key) {
                                ConfigScreen(
                                    navigateUp = {
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }

                            else -> NavEntry(Unit) { Text("?") }
                        }
                    }
                )
            }
        }
    }
}
