package net.dankrushen.danknn.danklayers

import net.dankrushen.danknn.DankConnection

class DankInputLayer(numNeurons: Int) : DankLayer(numNeurons), IDankInputLayer {
    override lateinit var outputLayer: IDankOutputLayer

    override val outputConnections: Array<DankConnection>
        get() = outputLayer.inputConnections
}
