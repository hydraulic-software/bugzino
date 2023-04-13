package dev.hydraulic.bugzino.frontend.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import dev.hydraulic.bugzino.frontend.screens.SignInScreen
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.inputStream

const val APP_BRAND_NAME = "Bugzino"

// TODO(refactor): This could be moved into a composition local for tests.
lateinit var TicketDatabase: DatabaseAccess

fun main() {
    val version = System.getProperty("app.version") ?: "Development"

    application {
        // app.dir is set when packaged to point at our collected inputs.
        val appIcon: BitmapPainter? = remember {
            System.getProperty("app.dir")
                ?.let { Paths.get(it, "icon-512.png") }
                ?.takeIf { it.exists() }
                ?.inputStream()
                ?.buffered()
                ?.use { BitmapPainter(loadImageBitmap(it)) }
        }

        Window(
            onCloseRequest = ::exitApplication, state = WindowState(
                position = WindowPosition(Alignment.Center),
                size = DpSize(1024.dp, 800.dp)
            ),
            title = APP_BRAND_NAME,
            icon = appIcon
        ) {
            MaterialTheme {
                Navigator(SignInScreen())
            }
        }
    }
}
