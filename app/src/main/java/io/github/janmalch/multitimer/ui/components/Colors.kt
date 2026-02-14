package io.github.janmalch.multitimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import io.github.janmalch.multitimer.models.Timer


// Source - https://stackoverflow.com/a/60479489
// Posted by burkinafaso3741, modified by community. See post 'Timeline' for change history
// Retrieved 2026-02-08, License - CC BY-SA 4.0
private fun Color.Companion.fromTimer(timer: Timer): Color =
    try {
        Color(("#" + timer.color.trimStart('#')).toColorInt())
    } catch (_: Exception) {
        Color.Gray
    }

@Composable
fun TimerColorBox(
    timer: Timer,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
) {

    Box(
        modifier = modifier
            .size(size)
            .background(Color.fromTimer(timer), RoundedCornerShape(4.dp))
    )
}