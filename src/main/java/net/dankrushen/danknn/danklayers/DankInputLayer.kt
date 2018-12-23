package net.dankrushen.danknn.danklayers

import net.dankrushen.danknn.DankConnection

class DankInputLayer(numNeurons: Int) : DankLayer(numNeurons), IDankInputLayer, Cloneable {
    override lateinit var outputLayer: IDankOutputLayer

    override val outputConnections: Array<DankConnection>
        get() = outputLayer.inputConnections

    public override fun clone(): DankInputLayer {
        val clone = DankInputLayer(neurons.size)

        for (i in neurons.indices) {
            neurons[i].copyTo(clone.neurons[i])
        }

        return clone
    }
}
