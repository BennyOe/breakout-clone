package io.bennyoe.ui

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import io.bennyoe.assets.BitmapFontAsset
import io.bennyoe.assets.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.graphics.color
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.skin
import ktx.style.slider
import ktx.style.textButton
import ktx.style.textField

var customSkin: Skin? = null

fun createSkin(assetStorage: AssetStorage) {
    val atlas = assetStorage[TextureAtlasAsset.UI.descriptor]
    val bearoutFont = assetStorage[BitmapFontAsset.BEAROUT.descriptor]
    val normalFont = assetStorage[BitmapFontAsset.NORMAL.descriptor]
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

    customSkin = skin(atlas) {
        label("default") {
            font = normalFont
        }
        textButton ("saveButton") {
            up = createColorDrawable(0.2f, 0.2f, 0.2f, 1f)
            down = createColorDrawable(0.1f, 0.1f, 0.1f, 1f)
            font = normalFont
        }

        textField("default") {
            font = normalFont
            fontColor = color(1f, 1f, 1f, 1f)
            background = createColorDrawable(0.2f, 0.2f, 0.2f, 1f)
            cursor = createColorDrawable(1f, 1f, 1f, 1f)
        }
        slider("default-horizontal") {
            background = createColorDrawable(0.2f, 0.2f, 0.2f, 1f)
            knob = createColorDrawable(1f, 1f, 1f, 1f).apply {
                setMinSize(20f, 20f)
            }
        }
    }
}


fun createColorDrawable(r: Float, g: Float, b: Float, a: Float): TextureRegionDrawable {
    val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    pixmap.setColor(color(r, g, b, a))
    pixmap.fill()

    val texture = Texture(pixmap)
    pixmap.dispose()

    return TextureRegionDrawable(TextureRegion(texture))
}
