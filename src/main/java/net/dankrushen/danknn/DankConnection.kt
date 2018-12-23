package net.dankrushen.danknn

import java.util.*

class DankConnection {
    var sourceNeuron: DankNeuron
    var destNeuron: DankNeuron

    var weight = Random().nextDouble() - 0.5

    val isDead: Boolean
        get() = weight == 0.0

    var errorDerivative = 0.0

    var accumulatedErrorDerivative = 0.0
    var numberAccumulatedDerivatives = 0

    constructor(neuronFrom: DankNeuron, neuronTo: DankNeuron, initWeight: Double) {
        this.sourceNeuron = neuronFrom
        this.destNeuron = neuronTo
        weight = initWeight
    }

    constructor(neuronFrom: DankNeuron, neuronTo: DankNeuron) {
        this.sourceNeuron = neuronFrom
        this.destNeuron = neuronTo
    }
}
