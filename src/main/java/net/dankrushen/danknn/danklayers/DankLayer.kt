package net.dankrushen.danknn.danklayers

import net.dankrushen.danknn.DankNeuron

abstract class DankLayer(numNeurons: Int) : IDankLayer {
    final override var neurons: Array<DankNeuron> = arrayOf()

    init {
        val neuronList = mutableListOf<DankNeuron>()

        for (i in 0 until numNeurons) {
            neuronList.add(DankNeuron(this))
        }

        neurons = neuronList.toTypedArray()
    }

    override fun setBias(bias: Double) {
        for (neuron in neurons) {
            neuron.bias = bias
        }
    }
}
