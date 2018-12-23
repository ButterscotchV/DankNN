package net.dankrushen.danknn

import net.dankrushen.danknn.dankgraphics.DankImageGrid
import net.dankrushen.danknn.dankgraphics.DankImageGrid.AutoSpacingType
import net.dankrushen.danknn.danklayers.DankLayer
import net.dankrushen.danknn.danklayers.IDankOutputLayer
import net.dankrushen.danknn.extensions.getStringBounds
import net.dankrushen.danknn.extensions.usesAntialiasing
import java.awt.*
import java.awt.image.BufferedImage
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

class DankNetworkVisualizer {
    var imageGrid: DankImageGrid

    private val network: DankNetwork

    private var displayWindow: JFrame? = null
    private var imageDisplay: JLabel? = null

    constructor(network: DankNetwork) {
        this.network = network
        this.imageGrid = DankImageGrid()
    }

    constructor(network: DankNetwork, width: Int, height: Int) {
        this.network = network
        this.imageGrid = DankImageGrid(width, height)
    }

    fun setDisplayEnabled(enable: Boolean) {
        if (enable) {
            if (displayWindow == null)
                displayWindow = JFrame("Network Visualizer Display")

            if (imageDisplay == null)
                imageDisplay = JLabel()

            if (!frameContainsComponent(displayWindow!!, imageDisplay!!))
                displayWindow!!.add(imageDisplay)

            displayWindow!!.pack()
            displayWindow!!.isVisible = true
        } else {
            if (displayWindow != null)
                displayWindow!!.isVisible = false
        }
    }

    fun setDisplayExitBehaviour(exitBehaviour: Int) {
        if (displayWindow != null)
            displayWindow!!.defaultCloseOperation = exitBehaviour
    }

    fun centerDisplay() {
        if (displayWindow != null)
            displayWindow!!.setLocationRelativeTo(null)
    }

    fun destroyDisplay() {
        if (displayWindow != null) {
            displayWindow!!.dispose()
            displayWindow = null
        }

        if (imageDisplay != null)
            imageDisplay = null
    }

    private fun frameContainsComponent(frame: JFrame, component: Component): Boolean {
        for (frameComponent in frame.components)
            if (frameComponent === component)
                return true

        return false
    }

    fun drawImage(): BufferedImage {
        val image = BufferedImage(imageGrid.width, imageGrid.height, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()

        val columnCount = network.getLayers().size * 2 - 1
        val rowCount = Math.max(1, getMostNeuronsInLayers(*network.getLayers()))

        imageGrid.columns = columnCount
        imageGrid.rows = rowCount

        val connectionRowCount = Math.max(1, getMostConnectionsInLayers(*network.getLayers()))

        val connectionImageGrid = imageGrid.clone()

        connectionImageGrid.rows = connectionRowCount
        connectionImageGrid.autoSpacingType = AutoSpacingType.NONE

        val strokeWidth = 6

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val decimalFormat = DecimalFormat("0.00000")
        decimalFormat.roundingMode = RoundingMode.CEILING

        for (x in 0 until Math.max(imageGrid.columns, connectionImageGrid.columns)) {
            val layer: DankLayer
            var outputLayer: IDankOutputLayer? = null
            val neuronLayer = x % 2 == 0

            if (neuronLayer) {
                layer = network.getLayers()[x / 2]
            } else {
                layer = network.getLayers()[(x + 1) / 2]
                outputLayer = layer as IDankOutputLayer
            }

            for (y in 0 until Math.max(imageGrid.rows, connectionImageGrid.rows)) {
                // Draw neuron layer every even x column
                if (neuronLayer) {
                    if (y >= imageGrid.rows)
                        continue

                    val drawSpace = imageGrid.getDrawSpaceAt(x, y)
                    graphics.clip = drawSpace

                    if (y < layer.neurons.size) {
                        // System.out.println("Neuron Draw Area: " + drawSpace); // Spacing debug

                        val neuron = layer.neurons[y]

                        drawOvalInternalBorder(graphics, Color.LIGHT_GRAY, Color(Math.min(1f, Math.max(0f, neuron.bias.toFloat())), 0f, 0f), strokeWidth, drawSpace)

                        val neuronValue = neuron.output
                        val neuronText = decimalFormat.format(neuronValue)

                        graphics.font = graphics.font.deriveFont(25f)

                        val stringBounds = graphics.getStringBounds(neuronText)

                        graphics.color = Color.BLACK
                        graphics.drawString(neuronText, Math.floor(drawSpace.centerX).toInt() - Math.floorDiv(stringBounds.width, 2), Math.floor(drawSpace.centerY).toInt() + Math.floorDiv(stringBounds.height, 2))

                        // Draw line to connection
                        graphics.clip = null
                        for (i in 0 until neuron.outputConnections.size) {
                            val connectionDrawSpace = imageGrid.getDrawSpaceAt(x, y)

                            graphics.stroke = BasicStroke(3f)
                            graphics.color = Color.DARK_GRAY

                            graphics.stroke = BasicStroke(1f)
                            graphics.color = Color.LIGHT_GRAY
                        }
                    }
                } else { // Draw connection layer every even x column
                    if (y >= connectionImageGrid.rows)
                        continue

                    val drawSpace = connectionImageGrid.getDrawSpaceAt(x, y)
                    graphics.clip = drawSpace

                    if (y < outputLayer!!.inputConnections.size) {
                        val connection = outputLayer.inputConnections[y]

                        val connectionText = decimalFormat.format(connection.weight)

                        graphics.font = graphics.font.deriveFont(25f)

                        val stringBounds = graphics.getStringBounds(connectionText)

                        graphics.color = Color.BLACK
                        graphics.drawString(connectionText, Math.floor(drawSpace.centerX).toInt() - Math.floorDiv(stringBounds.width, 2), Math.floor(drawSpace.centerY).toInt() + Math.floorDiv(stringBounds.height, 2))
                    }
                }
            }
        }

        graphics.clip = null

        graphics.stroke = BasicStroke(1f)
        graphics.color = Color.BLUE
        connectionImageGrid.drawGridLines(graphics)

        graphics.stroke = BasicStroke(1f)
        graphics.color = Color.RED
        imageGrid.drawGridLines(graphics)

        graphics.dispose()

        if (imageDisplay != null) {
            imageDisplay!!.icon = ImageIcon(image)

            displayWindow!!.pack()
        }

        return image
    }

    private fun drawOvalInternalBorder(graphics2D: Graphics2D, innerColour: Color, outerColour: Color, thickness: Int, x: Int, y: Int, width: Int, height: Int) {
        val ovalSpacing = Math.floorDiv(thickness, 2)
        val antialiasSpacing = if (graphics2D.usesAntialiasing) 1 else 0

        // Center
        graphics2D.color = innerColour
        graphics2D.stroke = BasicStroke(0f)
        graphics2D.fillOval(x + ovalSpacing, y + ovalSpacing, width - thickness - antialiasSpacing, height - thickness - antialiasSpacing)

        // Outline
        graphics2D.color = outerColour
        graphics2D.stroke = BasicStroke(thickness.toFloat())
        graphics2D.drawOval(x + ovalSpacing, y + ovalSpacing, width - thickness - antialiasSpacing, height - thickness - antialiasSpacing)
    }

    private fun drawOvalInternalBorder(graphics2D: Graphics2D, innerColour: Color, outerColour: Color, thickness: Int, bounds: Rectangle) {
        drawOvalInternalBorder(graphics2D, innerColour, outerColour, thickness, bounds.x, bounds.y, bounds.width, bounds.height)
    }

    private fun getMostNeuronsInLayers(vararg layers: DankLayer): Int {
        var highestCount = 0

        for (layer in layers) {
            if (layer.neurons.size > highestCount)
                highestCount = layer.neurons.size
        }

        return highestCount
    }

    private fun getMostConnectionsInLayers(vararg layers: DankLayer): Int {
        var highestCount = 0

        for (layer in layers) {
            if (layer is IDankOutputLayer) {
                val outLayer = layer as IDankOutputLayer
                if (outLayer.inputConnections.size > highestCount)
                    highestCount = outLayer.inputConnections.size
            }
        }

        return highestCount
    }
}
