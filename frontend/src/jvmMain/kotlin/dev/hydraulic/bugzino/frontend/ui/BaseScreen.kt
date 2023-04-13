package dev.hydraulic.bugzino.frontend.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen

abstract class BaseScreen : Screen {
    protected open val fontSize: TextUnit = 30.sp

    @Composable
    override fun Content() {
        Image(backgroundBitmap, "Abstract background image", Modifier.fillMaxSize().alpha(0.3f), contentScale = ContentScale.Crop)

        // TODO: Snackbar for errors

        CompositionLocalProvider(LocalTextStyle provides TextStyle(fontFamily = FontFamily(font), fontSize = fontSize)) {
            ScreenContent()
        }
    }

    @Composable
    abstract fun ScreenContent()

    companion object {
        private val backgroundBitmap = BaseScreen::class.java.getResourceAsStream("/background.jpg")!!.buffered().use {
            loadImageBitmap(it)
        }

        val font = Font("Assistant-VariableFont_wght.ttf")
    }
}
