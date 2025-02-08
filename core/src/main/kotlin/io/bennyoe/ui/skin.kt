package io.bennyoe.ui

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import io.bennyoe.assets.BitmapFontAsset
import io.bennyoe.assets.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.graphics.color
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.skin
import ktx.style.textField

fun createSkin(assetStorage: AssetStorage) {
    val atlas = assetStorage[TextureAtlasAsset.UI.descriptor]
    val bearoutFont = assetStorage[BitmapFontAsset.BEAROUT.descriptor]
    Scene2DSkin.defaultSkin = skin(atlas) {
        label("default") {
            font = bearoutFont
        }

        textField("default") {
            font = bearoutFont
            fontColor = color(1f, 1f, 1f, 1f)
            background = createColorDrawable(0.2f, 0.2f, 0.2f, 1f)
            cursor = createColorDrawable(1f, 1f, 1f, 1f)
        }
    }
}

fun createColorDrawable(r: Float, g: Float, b: Float, a: Float): TextureRegionDrawable {
    val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    pixmap.setColor(color(r, g, b, a)) // Farbe setzen
    pixmap.fill()

    val texture = Texture(pixmap)
    pixmap.dispose() // Speicher freigeben

    return TextureRegionDrawable(TextureRegion(texture))
}
