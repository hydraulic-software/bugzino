package dev.hydraulic.bugzino.frontend.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TicketSummaryCard(
    id: Int,
    title: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    offsetToTheSide: Boolean = false
) {
    val shape = RoundedCornerShape(8.dp)
    val offset = animateDpAsState(if (offsetToTheSide) 15.dp else 0.dp)

    Surface(
        contentColor = Color.White,
        shape = shape,
        elevation = 5.dp,
        modifier = Modifier
            .offset(offset.value)
            .clickable { onClick() }
            .fillMaxSize()
            .pointerHoverIcon(PointerIconDefaults.Hand)
    ) {
        Column(
            modifier = Modifier.background(backgroundColor).padding(15.dp)
        ) {
            Text(
                text = title.uppercase(),
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = "TI-$id",
                fontSize = 15.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )
        }
    }
}


// spec:1438fb94c2f8365c
