package net.dankrushen.danknn

import java.util.*

class DankConnection : Cloneable {
    var sourceNeuron: DankNeuron
    var destNeuron: DankNeuron

    var weight = Random().nextDouble() - 0.5

    val isDead: Boolean
        get() = weight == 0.0

    var errorDerivative = 0.0

    var accumulatedErrorDerivative = 0.0
    var numberAccumulatedDerivatives = 0

    constructor(sourceNeuron: DankNeuron, destNeuron: DankNeuron, initWeight: Double) {
        this.sourceNeuron = sourceNeuron
        this.destNeuron = destNeuron
        weight = initWeight
    }

    constructor(sourceNeuron: DankNeuron, destNeuron: DankNeuron) {
        this.sourceNeuron = sourceNeuron
        this.destNeuron = destNeuron
    }

    fun copyTo(connection: DankConnection): DankConnection {
        connection.weight = weight

        connection.errorDerivative = errorDerivative
        connection.accumulatedErrorDerivative = accumulatedErrorDerivative
        connection.numberAccumulatedDerivatives = numberAccumulatedDerivatives

        return connection
    }
}
