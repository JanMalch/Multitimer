package io.github.janmalch.multitimer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.janmalch.multitimer.ui.theme.MultiTimerTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mViewModel by viewModels<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiTimerTheme {
                Scaffold { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        Text("Hello")
                    }
                }
            }
        }
    }
}
