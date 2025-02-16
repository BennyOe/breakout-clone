import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import ktx.assets.disposeSafely

object TtfFactory {

    private var fontGenerator: FreeTypeFontGenerator? = null

    private fun loadFont(filePath: String, size: Int): BitmapFont {
        fontGenerator?.disposeSafely()
        fontGenerator = FreeTypeFontGenerator(Gdx.files.internal(filePath))

        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            this.size = size
        }

        val font = fontGenerator!!.generateFont(parameter)
        return font
    }

    fun monogramTtf(): BitmapFont {
        return loadFont("fonts/monogram.ttf", 50)
    }
}
