package dev.hydraulic.bugzino.frontend.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String = "",
    label: @Composable () -> Unit = { Text(labelText) },
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height((LocalTextStyle.current.fontSize.value * 3).dp)
            .shadow(5.dp, ROUNDED_CORNER_SHAPE)
            .clip(ROUNDED_CORNER_SHAPE)
    ) {
        val verticalScrollState = rememberScrollState(0)
        var focused by remember { mutableStateOf(false) }

        val backgroundColor by animateColorAsState(
            targetValue = if (focused) Color.White else Color(0xFFF2F2F2),
            animationSpec = tween(1000)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            label = label,
            shape = ROUNDED_CORNER_SHAPE,
            visualTransformation = visualTransformation,
            textStyle = textStyle,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.Black,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = Color(0xFFA8A8A8),
                backgroundColor = backgroundColor,
            ),
            modifier = Modifier
                .fillMaxSize()
                .also { if (!singleLine) it.verticalScroll(verticalScrollState) }
                .onFocusChanged { focused = it.isFocused }
        )

        if (!singleLine) {
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(verticalScrollState)
            )
        }
    }
}


// spec:25bd80e1017100de
