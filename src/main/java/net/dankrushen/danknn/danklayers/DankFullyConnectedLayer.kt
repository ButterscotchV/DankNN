package net.dankrushen.danknn.danklayers

import net.dankrushen.danknn.DankConnection
import java.util.*

class DankFullyConnectedLayer(numNeurons: Int, override val inputLayer: IDankInputLayer) : DankLayer(numNeurons), IDankConnectedLayer, IDankInputLayer, IDankOutputLayer {
    override val inputConnections: Array<DankConnection>
    override lateinit var outputLayer: IDankOutputLayer

    override val outputConnections: Array<DankConnection>
        get() = outputLayer.inputConnections

    init {

        inputLayer.outputLayer = this

        val inConnectionsList = ArrayList<DankConnection>()

        for (externalNeuron in inputLayer.neurons) {
            for (internalNeuron in neurons) {
                inConnectionsList.add(DankConnection(externalNeuron, internalNeuron))
            }
        }

        inputConnections = inConnectionsList.toTypedArray()
    }

    fun copyTo(outputLayer: DankFullyConnectedLayer): DankFullyConnectedLayer {
        for (i in neurons.indices) {
            neurons[i].copyTo(outputLayer.neurons[i])
        }

        for (i in inputConnections.indices) {
            inputConnections[i].copyTo(outputLayer.inputConnections[i])
        }

        return outputLayer
    }
}
