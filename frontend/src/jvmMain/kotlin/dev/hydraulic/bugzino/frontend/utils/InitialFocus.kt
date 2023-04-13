package dev.hydraulic.bugzino.frontend.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

/**
 * Gives the control that uses the modifier initial focus.
 */
@Composable
fun InitialFocus(modifier: Modifier = Modifier, content: @Composable (Modifier) -> Unit) {
    val requester = remember { FocusRequester() }
    content(modifier.focusRequester(requester))
    LaunchedEffect(Unit) {
        requester.requestFocus()
    }
}
