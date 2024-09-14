package io.github.katarem

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "anime-card-trader-client",
    ) {
        App()
    }
}