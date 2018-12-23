package net.dankrushen.danknn

import net.dankrushen.danknn.danklayers.*
import java.util.*

class DankNetwork(vararg layers: DankLayer) : Cloneable {
    private val layers = ArrayList<DankLayer>()

    val inputLayer: DankInputLayer
        get() = layers[0] as DankInputLayer

    val outputLayer: DankOutputLayer
        get() = layers[layers.size - 1] as DankOutputLayer

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

    fun setBias(bias: Double) {
        // Loop through every layer & set neuron values to 0
        for (layer in layers) {
            layer.setBias(bias)
        }
    }

    fun forwardProp(inputs: DoubleArray): DoubleArray {
        val inputNeurons = inputLayer.neurons

        // Set input layer values
        for (i in inputNeurons.indices) {
            inputNeurons[i].output = inputs[i]
        }

        // Loop through every layer past the input layer & calculate neuron values
        for (i in 1 until layers.size) {
            for (neuron in layers[i].neurons) {
                neuron.updateOutput()
            }
        }

        // Get output layer
        val outputNeurons = outputLayer.neurons
        val outputs = DoubleArray(outputNeurons.size)

        // Fill outputs values
        for (i in outputNeurons.indices) {
            outputs[i] = outputNeurons[i].output
        }

        return outputs
    }

    fun backProp(targets: DoubleArray): Double {
        val outputNeurons = outputLayer.neurons

        // Set output layer errors
        for (i in outputNeurons.indices) {
            val neuron = outputNeurons[i]
            neuron.outputDerivative = derivLossFunction(neuron.output, targets[i])
        }

        // Loop backwards through every layer except the input layer & calculate neuron errors
        for (i in layers.size - 1 downTo 1) {
            val layer = layers[i]

            for (neuron in layer.neurons) {
                neuron.inputDerivative = neuron.outputDerivative * derivActivationFunction(neuron.totalInput)
                neuron.accumulatedInputDerivative += neuron.inputDerivative
                neuron.numberAccumulatedDerivatives++
            }

            for (neuron in layer.neurons) {
                for (inputConnection in neuron.inputConnections) {
                    if (inputConnection.isDead)
                        continue

                    inputConnection.errorDerivative = neuron.inputDerivative * inputConnection.sourceNeuron.output
                    inputConnection.accumulatedErrorDerivative += inputConnection.errorDerivative
                    inputConnection.numberAccumulatedDerivatives++
                }
            }

            if (i == 1)
                continue

            val lastLayer = layers[i - 1]
            for (neuron in lastLayer.neurons) {
                neuron.outputDerivative = 0.0

                for (outputConnection in neuron.outputConnections) {
                    neuron.outputDerivative += outputConnection.weight * outputConnection.destNeuron.inputDerivative
                }
            }
        }

        return calculateLoss(targets)
    }

    fun calculateLoss(targets: DoubleArray): Double {
        var loss = 0.0

        val outputNeurons = outputLayer.neurons

        // Set output layer errors
        for (i in outputNeurons.indices) {
            loss += lossFunction(outputNeurons[i].outputDerivative, targets[i])
        }

        return loss
    }

    fun updateWeights(learningRate: Double = 1.0) {
        for (i in 1 until layers.size) {
            val layer = layers[i]

            for (neuron in layer.neurons) {
                if (neuron.numberAccumulatedDerivatives > 0) {
                    neuron.bias -= (learningRate * neuron.accumulatedInputDerivative) / neuron.numberAccumulatedDerivatives
                    neuron.accumulatedInputDerivative = 0.0
                    neuron.numberAccumulatedDerivatives = 0
                }

                for (inputConnection in neuron.inputConnections) {
                    if (inputConnection.isDead)
                        continue

                    if (inputConnection.numberAccumulatedDerivatives > 0) {
                        inputConnection.weight = inputConnection.weight - (learningRate / inputConnection.numberAccumulatedDerivatives) * inputConnection.accumulatedErrorDerivative
                        inputConnection.accumulatedErrorDerivative = 0.0
                        inputConnection.numberAccumulatedDerivatives = 0
                    }
                }
            }
        }
    }

    companion object {

        fun activationFunction(neuronVal: Double): Double = 1.0 / (1.0 + Math.exp(-neuronVal))

        fun derivActivationFunction(neuronVal: Double): Double {
            val output = activationFunction(neuronVal)
            return output * (1.0 - output)
        }

        fun lossFunction(output: Double, target: Double): Double = 0.5 * Math.pow(output - target, 2.0)

        fun derivLossFunction(output: Double, target: Double): Double = output - target
    }

    public override fun clone(): DankNetwork {
        val layersClone = mutableListOf<DankLayer>()

        layersClone.add(inputLayer.clone())

        for (i in 1 until layers.size) {
            val origLayer = layers[i]
            val lastLayer = layersClone[layersClone.size - 1]

            if (origLayer is DankFullyConnectedLayer) {
                val newLayer = DankFullyConnectedLayer(origLayer.neurons.size, lastLayer as IDankInputLayer)

                origLayer.copyTo(newLayer)

                layersClone.add(newLayer)
            } else if (origLayer is DankOutputLayer) {
                val newLayer = DankOutputLayer(origLayer.neurons.size, lastLayer as IDankInputLayer)

                origLayer.copyTo(newLayer)

                layersClone.add(newLayer)
            }
        }

        return DankNetwork(*layersClone.toTypedArray())
    }
}
