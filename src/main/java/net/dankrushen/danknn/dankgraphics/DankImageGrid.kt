package net.dankrushen.danknn.dankgraphics

import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.geom.Line2D
import kotlin.math.roundToInt

class DankImageGrid : Cloneable {
    var width = 1280
    var height = 720

    var columns = 1
    var rows = 1

    // Imagine these being multiplied by 2, this is used on both sides of the column unless
    var columnSpacing = 2
    var rowSpacing = 2

    var extraAutoSpacingPercent = 0.0

    var autoSpacingType = AutoSpacingType.NONE

    val autoSpacing: DankSpacing
        get() {
            return when (autoSpacingType) {
                DankImageGrid.AutoSpacingType.NONE -> DankSpacing(columnSpacing, rowSpacing)
                DankImageGrid.AutoSpacingType.SQUARE -> squareSpacing
                DankImageGrid.AutoSpacingType.SQUARE_PLUS_DEFINED -> getSquareSpacing(columnSpacing, rowSpacing)
                DankImageGrid.AutoSpacingType.SQUARE_PLUS_PERCENT -> getSquareSpacingPercent(extraAutoSpacingPercent)
            }

            return DankSpacing(columnSpacing, rowSpacing)
        }

    val squareSpacing: DankSpacing
        get() = getSquareSpacing(0)

    val pixelsPerColumn: Double
        get() = width.toDouble() / columns.toDouble()

    val pixelsPerRow: Double
        get() = height.toDouble() / rows.toDouble()

    enum class AutoSpacingType {
        NONE,
        SQUARE,
        SQUARE_PLUS_DEFINED,
        SQUARE_PLUS_PERCENT
    }

    constructor()

    constructor(width: Int, height: Int) {

        this.width = width
        this.height = height
    }

    constructor(width: Int, height: Int, columns: Int, rows: Int) {

        this.width = width
        this.height = height

        this.columns = columns
        this.rows = rows
    }

    constructor(width: Int, height: Int, columns: Int, rows: Int, columnSpacing: Int, rowSpacing: Int) {

        this.width = width
        this.height = height

        this.columns = columns
        this.rows = rows

        this.columnSpacing = columnSpacing
        this.rowSpacing = rowSpacing
    }

    inner class DankSpacing(val columnSpacing: Int, val rowSpacing: Int)

    fun setColumnSpacingPercent(columnSpacingPercent: Double) {
        columnSpacing = Math.round(pixelsPerColumn * (columnSpacingPercent / 100.0) / 2.0).toInt()
    }

    fun setRowSpacingPercent(rowSpacingPercent: Double) {
        rowSpacing = Math.round(pixelsPerRow * (rowSpacingPercent / 100.0) / 2.0).toInt()
    }

    fun getSquareSpacing(extraColumnSpacing: Int, extraRowSpacing: Int): DankSpacing {
        val minLength = Math.min(pixelsPerColumn, pixelsPerRow)

        val columnSpacing = Math.floor((pixelsPerColumn - minLength) / 2.0).roundToInt() + extraColumnSpacing
        val rowSpacing = Math.floor((pixelsPerRow - minLength) / 2.0).roundToInt() + extraRowSpacing

        return DankSpacing(columnSpacing, rowSpacing)
    }

    fun getSquareSpacing(extraSpacing: Int): DankSpacing {
        return getSquareSpacing(extraSpacing, extraSpacing)
    }

    fun getSquareSpacingPercent(extraSpacingPercent: Double): DankSpacing {
        val minLength = Math.min(pixelsPerColumn, pixelsPerRow)

        return getSquareSpacing(Math.round(minLength * (extraSpacingPercent / 100.0) / 2.0).toInt())
    }

    fun getDrawSpaceAt(column: Int, row: Int): Rectangle {
        val spacing = autoSpacing

        val x = column * pixelsPerColumn + spacing.columnSpacing
        val y = row * pixelsPerRow + spacing.rowSpacing

        val width = pixelsPerColumn - spacing.columnSpacing * 2.0
        val height = pixelsPerRow - spacing.rowSpacing * 2.0

        return Rectangle(Math.floor(x).roundToInt(), Math.floor(y).roundToInt(), Math.floor(width).roundToInt(), Math.floor(height).roundToInt())
    }

    fun getColumnLineAt(column: Int): Line2D {
        val x = pixelsPerColumn * column
        return Line2D.Float(x.toFloat(), 0f, x.toFloat(), height.toFloat())
    }

    fun getRowLineAt(row: Int): Line2D {
        val y = pixelsPerRow * row
        return Line2D.Float(0f, y.toFloat(), width.toFloat(), y.toFloat())
    }

    fun drawGridLines(graphics: Graphics2D) {
        for (x in 0 until columns) {
            val line = getColumnLineAt(x)
            graphics.draw(line)
        }

        for (y in 0 until rows) {
            val line = getRowLineAt(y)
            graphics.draw(line)
        }
    }

    public override fun clone(): DankImageGrid {
        val clone = DankImageGrid()

        clone.width = width
        clone.height = height

        clone.columns = columns
        clone.rows = rows

        clone.columnSpacing = columnSpacing
        clone.rowSpacing = rowSpacing

        clone.extraAutoSpacingPercent = extraAutoSpacingPercent

        clone.autoSpacingType = autoSpacingType

        return clone
    }
}
