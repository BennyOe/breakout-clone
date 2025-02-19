package io.bennyoe.utillity

import com.badlogic.gdx.utils.Json
import io.bennyoe.Main
import ktx.preferences.flush
import ktx.preferences.set

class HighScoreManager(private val game: Main) {

    fun loadHighScores(): MutableList<PlayerHighscore> {
        val json = Json()
        val storedScores = game.preferences.getString("highscore", "[]")

        val highscoreList: MutableList<PlayerHighscore> = try {
            json.fromJson(Array<PlayerHighscore>::class.java, storedScores).toMutableList()
        } catch (e: Exception) {
            println("Fehler beim Laden der Highscores: ${e.message}")
            mutableListOf()
        }

        return highscoreList.sortedByDescending { it.score }.take(10).toMutableList()
    }

    fun getLowestHighScore(): Int {
        return loadHighScores().lastOrNull()?.score ?: 0
    }

    fun saveHighScore(playerHighscore: PlayerHighscore) {
        val json = Json()
        val prevHighscore = loadHighScores()
        prevHighscore.add(playerHighscore)

        game.preferences.flush {
            this["highscore"] = json.toJson(prevHighscore)
        }
    }
}

data class PlayerHighscore(
    val name: String = "",
    val score: Int = 0
)
