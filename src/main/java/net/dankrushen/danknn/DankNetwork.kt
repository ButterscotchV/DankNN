package net.dankrushen.danknn

import net.dankrushen.danknn.danklayers.DankLayer
import net.dankrushen.danknn.danklayers.IDankInputLayer
import net.dankrushen.danknn.danklayers.IDankOutputLayer
import java.util.*

class DankNetwork(vararg layers: DankLayer) {
    private val layers = ArrayList<DankLayer>()

    val inputLayer: IDankInputLayer
        get() = layers[0] as IDankInputLayer

    val outputLayer: IDankOutputLayer
        get() = layers[layers.size - 1] as IDankOutputLayer

    init {
        for (layer in layers) {
            this.layers.add(layer) // Add each user-defined layer
        }
    }

    fun getLayers(): Array<DankLayer> {
        return layers.toTypedArray()
    }

    fun resetAllNeurons() {
        // Loop through every layer & set neuron values to 0
        for (layer in layers) {
            for (neuron in layer.neurons) {
                neuron.reset()
            }
        }
    }

    fun forwardprop(inputs: DoubleArray): DoubleArray {
        resetAllNeurons() // Reset all neurons to 0

        val inputNeurons = inputLayer.neurons

        // Set input layer values
        for (i in inputNeurons.indices) {
            inputNeurons[i].value = inputs[i]
        }

        // Loop through every layer past the input layer & calculate neuron values
        for (i in 1 until layers.size) {
            for (connection in (layers[i] as IDankOutputLayer).inputConnections) {
                connection.passValue()
            }
        }

        // Get output layer
        val outputNeurons = outputLayer.neurons
        val outputs = DoubleArray(outputNeurons.size)

        // Fill outputs values
        for (i in outputNeurons.indices) {
            outputs[i] = outputNeurons[i].activatedValue
        }

        return outputs
    }

    fun backprop(errors: DoubleArray, learningRate: Double = 1.0): Double {
        val outputNeurons = outputLayer.neurons

        // Set output layer errors
        for (i in outputNeurons.indices) {
            outputNeurons[i].error = errors[i]
        }

        // Loop backwards through every layer except the input layer & calculate neuron errors
        for (i in layers.size - 1 downTo 1) {
            for (connection in (layers[i] as IDankOutputLayer).inputConnections) {
                connection.passError()
                connection.adjustFromError(learningRate)
            }
        }

        return calculateLoss()
    }

    fun calculateLoss(): Double {
        var loss = 0.0

        val outputNeurons = outputLayer.neurons

        // Set output layer errors
        for (i in outputNeurons.indices) {
            loss += lossFunction(outputNeurons[i].error)
        }

        return loss
    }

    companion object {

        fun activationFunction(neuronVal: Double): Double = 1.0 / (1.0 + Math.exp(-neuronVal))

        fun derivActivationFunction(neuronVal: Double): Double = neuronVal * (1.0 - neuronVal)

        fun lossFunction(errorVal: Double): Double = errorVal * errorVal / 2.0

        fun derivLossFunction(errorVal: Double): Double = -errorVal
    }
}
