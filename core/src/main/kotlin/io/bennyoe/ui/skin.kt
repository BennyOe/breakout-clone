package io.bennyoe.ui

import io.bennyoe.assets.BitmapFontAsset
import io.bennyoe.assets.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.skin

fun createSkin(assetStorage: AssetStorage) {
    val atlas = assetStorage[TextureAtlasAsset.UI.descriptor]
    val bearoutFont = assetStorage[BitmapFontAsset.BEAROUT.descriptor]
    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        label("default") {
            font = bearoutFont
        }
    }
}
