package net.dankrushen.danknn.extensions

import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.font.GlyphVector
import java.awt.geom.Line2D
import java.math.BigDecimal
import java.math.RoundingMode

val Graphics2D.usesAntialiasing: Boolean
    get() {
        return renderingHints.containsKey(RenderingHints.KEY_ANTIALIASING) && renderingHints[RenderingHints.KEY_ANTIALIASING] === RenderingHints.VALUE_ANTIALIAS_ON
    }

fun Graphics2D.drawLine(line: Line2D, roundingMode: RoundingMode = RoundingMode.HALF_DOWN) {
    val x1 = BigDecimal.valueOf(line.x1).setScale(0, roundingMode).toInt()
    val y1 = BigDecimal.valueOf(line.y1).setScale(0, roundingMode).toInt()

    val x2 = BigDecimal.valueOf(line.x2).setScale(0, roundingMode).toInt()
    val y2 = BigDecimal.valueOf(line.y2).setScale(0, roundingMode).toInt()

    drawLine(x1, y1, x2, y2)
}

fun Graphics2D.drawGlyphVector(glyphVector: GlyphVector, x: Int, y: Int) {
    drawGlyphVector(glyphVector, x.toFloat(), y.toFloat())
}

fun Graphics2D.draw(shape: Shape, x: Int, y: Int) {
    val lastTrans = transform

    translate(x, y)
    draw(shape)

    transform = lastTrans
}

fun Graphics2D.getStringBounds(str: String, x: Float = 0f, y: Float = 0f): Rectangle {
    val frc = fontRenderContext
    val gv = font.createGlyphVector(frc, str)

    return gv.getPixelBounds(null, x, y)
}

fun Graphics2D.getStringGlyphVector(str: String): GlyphVector {
    val frc = fontRenderContext

    return font.createGlyphVector(frc, str)
}