package dev.hydraulic.bugzino.frontend.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.placeholder.material.placeholder as materialPlaceholder

const val ROUNDING = 8
val ROUNDED_CORNER_SHAPE = RoundedCornerShape(ROUNDING.dp)

fun Modifier.placeholder(condition: Boolean) = materialPlaceholder(
    condition,
    Color(0xFFBBBBBB),
    highlight = PlaceholderHighlight.shimmer(Color.LightGray),
    shape = ROUNDED_CORNER_SHAPE
)
