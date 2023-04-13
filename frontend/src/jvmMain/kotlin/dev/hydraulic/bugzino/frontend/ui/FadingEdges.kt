package dev.hydraulic.bugzino.frontend.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import kotlin.math.abs

fun Modifier.verticalFadingEdge(
    lazyListState: LazyListState,
    length: Dp,
    edgeColor: Color? = null,
) = composed(
    debugInspectorInfo {
        name = "length"
        value = length
    }
) {
    val color = edgeColor ?: MaterialTheme.colors.surface

    drawWithContent {
        val topFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo.run {
                val firstItem = visibleItemsInfo.first()
                when {
                    visibleItemsInfo.size in 0..1 -> 0f
                    firstItem.index > 0 -> 1f // Added
                    firstItem.offset == viewportStartOffset -> 0f
                    firstItem.offset < viewportStartOffset -> firstItem.run {
                        abs(offset) / size.toFloat()
                    }
                    else -> 1f
                }
            }.coerceAtMost(1f) * length.value
        }
        val bottomFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo.run {
                val lastItem = visibleItemsInfo.last()
                when {
                    visibleItemsInfo.size in 0..1 -> 0f
                    lastItem.index < totalItemsCount - 1 -> 1f // Added
                    lastItem.offset + lastItem.size <= viewportEndOffset -> 0f // added the <=
                    lastItem.offset + lastItem.size > viewportEndOffset -> lastItem.run {
                        (size - (viewportEndOffset - offset)) / size.toFloat()  // Fixed the percentage computation
                    }
                    else -> 1f
                }
            }.coerceAtMost(1f) * length.value
        }

        drawContent()

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    color,
                    Color.Transparent,
                ),
                startY = 0f,
                endY = topFadingEdgeStrength,
            ),
            size = Size(
                this.size.width,
                topFadingEdgeStrength
            ),
            blendMode = BlendMode.DstOut
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    color,
                ),
                startY = size.height - bottomFadingEdgeStrength,
                endY = size.height,
            ),
            topLeft = Offset(x = 0f, y = size.height - bottomFadingEdgeStrength),
            blendMode = BlendMode.DstOut
        )
    }
}
