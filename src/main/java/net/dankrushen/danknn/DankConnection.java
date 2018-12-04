package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.DankInputLayer;

public class DankConnection {
    DankNeuron neuronFrom;
    DankNeuron neuronTo;

    double weight = 1;

    public DankConnection(DankNeuron neuronFrom, DankNeuron neuronTo, double initWeight) {
        this.neuronFrom = neuronFrom;
        this.neuronTo = neuronTo;
        weight = initWeight;
    }

    public DankConnection(DankNeuron neuronFrom, DankNeuron neuronTo) {
        this.neuronFrom = neuronFrom;
        this.neuronTo = neuronTo;
    }

    public void passValue() {
        neuronTo.addValue((neuronFrom.getParentLayer() instanceof DankInputLayer ? neuronFrom.getValue() : neuronFrom.getActivatedValue()) * weight);
    }

    public void passError() {
        neuronFrom.addError(neuronTo.getError() * weight);
    }
}
