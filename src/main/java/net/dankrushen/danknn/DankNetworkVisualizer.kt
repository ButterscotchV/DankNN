package net.dankrushen.danknn

import net.dankrushen.danknn.dankgraphics.DankImageGrid
import net.dankrushen.danknn.dankgraphics.DankImageGrid.AutoSpacingType
import net.dankrushen.danknn.danklayers.DankLayer
import net.dankrushen.danknn.danklayers.IDankInputLayer
import net.dankrushen.danknn.danklayers.IDankOutputLayer
import net.dankrushen.danknn.extensions.*
import java.awt.*
import java.awt.geom.Line2D
import java.awt.image.BufferedImage
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.math.roundToInt

class DankNetworkVisualizer {
    var imageGrid: DankImageGrid

    private val network: DankNetwork

    private var displayWindow: JFrame? = null
    private var imageDisplay: JLabel? = null

    var neuronBorderWidthPercent = 6.5

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

    fun drawImage(network: DankNetwork, extraData: String = ""): BufferedImage {
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
        connectionImageGrid.setColumnSpacingPercent(35.0)
        connectionImageGrid.setRowSpacingPercent(30.0)

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val decimalFormat = DecimalFormat("0.00000")
        decimalFormat.roundingMode = RoundingMode.CEILING

        graphics.stroke = BasicStroke(1f)
        graphics.color = Color.BLUE
        connectionImageGrid.drawGridLines(graphics)

        graphics.stroke = BasicStroke(1f)
        graphics.color = Color.RED
        imageGrid.drawGridLines(graphics)

        for (x in 0 until Math.max(imageGrid.columns, connectionImageGrid.columns)) {
            var layer: DankLayer
            var outputLayer: IDankOutputLayer? = null
            var inputLayer: IDankInputLayer? = null
            val neuronLayer = x % 2 == 0

            if (neuronLayer) {
                layer = network.getLayers()[x / 2]
            } else {
                layer = network.getLayers()[(x + 1) / 2]
                outputLayer = layer as IDankOutputLayer

                layer = network.getLayers()[(x - 1) / 2]
                inputLayer = layer as IDankInputLayer
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

                        val maxMinBias = getMostDistantBias(network).toFloat()
                        val biasColour = Color(Math.min(1f, Math.max(0f, neuron.bias.toFloat() / maxMinBias)), 0f, -Math.min(0f, Math.max(-1f, neuron.bias.toFloat() / maxMinBias)))
                        val borderThickness = Math.floor(Math.min(drawSpace.width, drawSpace.height) * (neuronBorderWidthPercent / 100.0)).roundToInt()
                        drawOvalInternalBorder(graphics, Color.LIGHT_GRAY, biasColour, borderThickness, drawSpace)

                        val neuronValue = neuron.output
                        val neuronText = decimalFormat.format(neuronValue)

                        graphics.font = graphics.font.deriveFont(25f)
                        graphics.color = Color.BLACK

                        val stringGlyphVector = graphics.getStringGlyphVector(neuronText)
                        val stringBounds = stringGlyphVector.getPixelBounds()

                        graphics.drawGlyphVector(stringGlyphVector, Math.floor(drawSpace.centerX).toInt() - Math.floorDiv(stringBounds.width, 2), Math.floor(drawSpace.centerY).toInt() + Math.floorDiv(stringBounds.height, 2))
                    }
                } else { // Draw connection layer every even x column
                    if (y >= connectionImageGrid.rows)
                        continue

                    val drawSpace = connectionImageGrid.getDrawSpaceAt(x, y)

                    if (y < outputLayer!!.inputConnections.size) {
                        val connection = outputLayer.inputConnections[y]

                        // Draw the connection lines
                        graphics.clip = null
                        val sourceNeuronDrawSpace = imageGrid.getDrawSpaceAt(x - 1, inputLayer!!.neurons.indexOf(connection.sourceNeuron))
                        val destNeuronDrawSpace = imageGrid.getDrawSpaceAt(x + 1, outputLayer!!.neurons.indexOf(connection.destNeuron))

                        val inLine = Line2D.Double(sourceNeuronDrawSpace.maxX, sourceNeuronDrawSpace.centerY, drawSpace.minX, drawSpace.centerY)
                        val outLine = Line2D.Double(drawSpace.maxX, drawSpace.centerY, destNeuronDrawSpace.minX, destNeuronDrawSpace.centerY)

                        graphics.stroke = BasicStroke(6f)
                        graphics.color = Color.DARK_GRAY
                        graphics.draw(inLine)

                        graphics.stroke = BasicStroke(2f)
                        graphics.color = Color.LIGHT_GRAY
                        graphics.draw(inLine)

                        graphics.stroke = BasicStroke(6f)
                        graphics.color = Color.DARK_GRAY
                        graphics.draw(outLine)

                        graphics.stroke = BasicStroke(2f)
                        graphics.color = Color.LIGHT_GRAY
                        graphics.draw(outLine)

                        graphics.clip = drawSpace
                        val connectionText = decimalFormat.format(connection.weight)

                        graphics.font = graphics.font.deriveFont(25f)
                        graphics.color = Color.BLACK

                        val stringGlyphVector = graphics.getStringGlyphVector(connectionText)
                        val stringBounds = stringGlyphVector.getPixelBounds()

                        graphics.drawGlyphVector(stringGlyphVector, Math.floor(drawSpace.centerX).toInt() - Math.floorDiv(stringBounds.width, 2), Math.floor(drawSpace.centerY).toInt() + Math.floorDiv(stringBounds.height, 2))
                    }
                }
            }
        }

        graphics.clip = null

        if (extraData != "") {
            graphics.font = graphics.font.deriveFont(20f)

            val stringGlyphVector = graphics.getStringGlyphVector(extraData)
            val stringBounds = stringGlyphVector.getPixelBounds()

            graphics.stroke = BasicStroke(5f)
            graphics.color = Color.BLACK

            var shape = stringGlyphVector.outline
            graphics.draw(shape, 0, stringBounds.height)

            graphics.color = Color.WHITE

            graphics.drawGlyphVector(stringGlyphVector, 0, stringBounds.height)
        }

        graphics.dispose()

        if (imageDisplay != null) {
            imageDisplay!!.icon = ImageIcon(image)

            displayWindow!!.pack()
        }

        return image
    }

    private fun getMostDistantBias(network: DankNetwork): Double {
        var highestVal = 0.0

        for (layer in network.getLayers()) {
            for (neuron in layer.neurons) {
                if (Math.abs(neuron.bias) > Math.abs(highestVal))
                    highestVal = neuron.bias
            }
        }

        return highestVal
    }

    fun drawImage(extraData: String = ""): BufferedImage {
        return drawImage(network, extraData)
    }

    fun drawImageAsync(network: DankNetwork, extraData: String = "") {
        val networkClone = network.clone()

        Thread {
            drawImage(networkClone, extraData)
        }.start()
    }

    fun drawImageAsync(extraData: String = "") {
        drawImageAsync(network, extraData)
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
