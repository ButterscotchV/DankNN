package net.dankrushen.danknn.danklayers

import net.dankrushen.danknn.DankConnection
import java.util.*

class DankOutputLayer(numNeurons: Int, override val inputLayer: IDankInputLayer) : DankLayer(numNeurons), IDankOutputLayer {
    override val inputConnections: Array<DankConnection>

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
}
