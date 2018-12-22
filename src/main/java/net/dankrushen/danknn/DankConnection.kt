package net.dankrushen.danknn

import net.dankrushen.danknn.danklayers.DankInputLayer
import net.dankrushen.danknn.danklayers.DankOutputLayer
import java.util.*

class DankConnection {
    var neuronFrom: DankNeuron
    var neuronTo: DankNeuron

    var weight = Random().nextDouble() - 0.5

    constructor(neuronFrom: DankNeuron, neuronTo: DankNeuron, initWeight: Double) {
        this.neuronFrom = neuronFrom
        this.neuronTo = neuronTo
        weight = initWeight
    }

    constructor(neuronFrom: DankNeuron, neuronTo: DankNeuron) {
        this.neuronFrom = neuronFrom
        this.neuronTo = neuronTo
    }

    fun passError() {
        neuronFrom.error += calcError() * weight
    }

    fun passValue() {
        neuronTo.value += (if (neuronFrom.parentLayer is DankInputLayer) neuronFrom.value else neuronFrom.activatedValue) * weight
        //System.out.println("Passed through value of " + ((neuronFrom.getParentLayer() instanceof DankInputLayer ? neuronFrom.value : neuronFrom.getActivatedValue()) * weight) + " from " + neuronFrom.value + " with " + weight);
    }

    fun calcError(): Double {
        return (if (neuronTo.parentLayer is DankOutputLayer) DankNetwork.derivLossFunction(neuronTo.error) else neuronTo.error) * DankNetwork.derivActivationFunction(neuronTo.activatedValue)
    }

    fun adjustFromError(learningRate: Double = 1.0) {
        val deltaTo = calcError()
        weight -= deltaTo * neuronFrom.activatedValue * learningRate
        neuronTo.bias -= deltaTo * learningRate
    }
}
