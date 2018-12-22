package net.dankrushen.danknn.extensions

import java.awt.Graphics2D
import java.awt.geom.Line2D
import java.math.BigDecimal
import java.math.RoundingMode

fun Graphics2D.drawLine(line: Line2D, roundingMode: RoundingMode = RoundingMode.HALF_DOWN) {
    val x1 = BigDecimal.valueOf(line.x1).setScale(0, roundingMode).toInt()
    val y1 = BigDecimal.valueOf(line.y1).setScale(0, roundingMode).toInt()

    val x2 = BigDecimal.valueOf(line.x2).setScale(0, roundingMode).toInt()
    val y2 = BigDecimal.valueOf(line.y2).setScale(0, roundingMode).toInt()

    drawLine(x1, y1, x2, y2)
}