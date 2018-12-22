package net.dankrushen.danknn

import net.dankrushen.danknn.danklayers.*
import java.util.*

class DankNetworkBuilder(inputNeuronCount: Int) {
    private var builtNetwork: DankNetwork? = null
    private val layers = ArrayList<DankLayer>()

    init {
        layers.add(DankInputLayer(inputNeuronCount)) // Begin layers
    }

    fun addLayer(numNeurons: Int) {
        layers.add(DankFullyConnectedLayer(numNeurons, layers[layers.size - 1] as IDankInputLayer))
    }

    fun buildNetwork(outputNeuronCount: Int): DankNetwork {
        if (builtNetwork == null) {
            layers.add(DankOutputLayer(outputNeuronCount, layers[layers.size - 1] as IDankInputLayer))

            builtNetwork = DankNetwork(*layers.toTypedArray())
        }

        return builtNetwork as DankNetwork
    }
}
