@file:JvmName("Lwjgl3Launcher")

package io.bennyoe.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.bennyoe.Main

const val GAME_WIDTH = 48 * 32
const val GAME_HEIGHT = 27 * 32

fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return
    Lwjgl3Application(Main(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("GdxHelloWorld")
        setWindowedMode(GAME_WIDTH, GAME_HEIGHT)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
