package net.dankrushen.danknn.extensions

import java.awt.Rectangle
import java.awt.font.GlyphVector

fun GlyphVector.getPixelBounds(): Rectangle {
    return this.getPixelBounds(null, 0f, 0f)
}