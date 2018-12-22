package net.dankrushen.danknn

import net.dankrushen.danknn.danklayers.DankLayer
import net.dankrushen.danknn.danklayers.IDankInputLayer
import net.dankrushen.danknn.danklayers.IDankOutputLayer
import java.util.*

class DankNeuron(val parentLayer: DankLayer) {

    private var _value: Double = 0.0
    var value: Double
        get() = _value + bias
        set(value) {
            _value = value
        }
    val activatedValue: Double
        get() = DankNetwork.activationFunction(value)
    var bias: Double = 0.0
    var error: Double = 0.0
    var iters: Int = 0

    val inputConnections: Array<DankConnection>
        get() {
            val connections = ArrayList<DankConnection>()

            if (parentLayer is IDankOutputLayer) {
                for (connection in (parentLayer as IDankOutputLayer).inputConnections) {
                    if (connection.neuronTo === this) {
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
                    if (connection.neuronFrom === this) {
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
        value = 0.0
        bias = 0.0
        error = 0.0
        iters = 0
    }
}
