package net.dankrushen.danknn

import net.dankrushen.danknn.danklayers.DankLayer
import net.dankrushen.danknn.danklayers.IDankInputLayer
import net.dankrushen.danknn.danklayers.IDankOutputLayer
import java.util.*

class DankNeuron(val parentLayer: DankLayer) {

    var bias = 0.0

    var totalInput = 0.0
    var output = 0.0

    var outputDerivative = 0.0
    var inputDerivative = 0.0

    var accumulatedInputDerivative = 0.0
    var numberAccumulatedDerivatives = 0

    val inputConnections: Array<DankConnection>
        get() {
            val connections = ArrayList<DankConnection>()

            if (parentLayer is IDankOutputLayer) {
                for (connection in (parentLayer as IDankOutputLayer).inputConnections) {
                    if (connection.destNeuron === this) {
                        connections.add(connection)
                    }
                }
            }

            return connections.toTypedArray()
        }

    val outputConnections: Array<DankConnection>
        get() {
            val connections = ArrayList<DankConnection>()

            if (parentLayer is IDankInputLayer) {
                for (connection in (parentLayer as IDankInputLayer).outputConnections) {
                    if (connection.sourceNeuron === this) {
                        connections.add(connection)
                    }
                }
            }

            return connections.toTypedArray()
        }

    init {
        reset()
    }

    fun reset() {
        totalInput = 0.0
        output = 0.0

        outputDerivative = 0.0
        inputDerivative = 0.0

        accumulatedInputDerivative = 0.0
        numberAccumulatedDerivatives = 0
    }

    fun updateOutput(): Double {
        totalInput = bias

        for (connection in inputConnections) {
            totalInput += connection.weight * connection.sourceNeuron.output
        }

        output = DankNetwork.activationFunction(totalInput)
        return output
    }
}
