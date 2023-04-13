package dev.hydraulic.bugzino.frontend.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A button that when clicked will invoke [action] in a background thread, invoking [onSuccess] on the UI thread with the result if it
 * succeeds and setting [errorMessage] based on the exception message if it fails. Whilst the action is in progress, a spinner is placed
 * inside the button and animated in and out. [isWorking] contains whether the action is currently in flight.
 */
@Composable
fun <T> IOButton(
    buttonText: String,
    action: suspend () -> T,
    onSuccess: (T) -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    errorMessage: MutableState<String?> = remember { mutableStateOf(null) },
    isWorking: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    if (isWorking.value) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    action()
                }
            }.onSuccess {
                errorMessage.value = null
                onSuccess(it)
            }.onFailure { ex ->
                errorMessage.value = ex.also { it.printStackTrace() }.localizedMessage
            }
            isWorking.value = false
        }
    }
    Button(
        onClick = { isWorking.value = true },
        enabled = enabled && !isWorking.value,
        modifier = modifier,
    ) {
        Row {
            AnimatedVisibility(visible = isWorking.value) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp)
                        .size(20.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(text = buttonText)
        }
    }
}
