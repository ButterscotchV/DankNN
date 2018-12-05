package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.DankInputLayer;

public class DankConnection {
    DankNeuron neuronFrom;
    DankNeuron neuronTo;

    double weight = 0;

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
        //System.out.println("Passed through value of " + ((neuronFrom.getParentLayer() instanceof DankInputLayer ? neuronFrom.getValue() : neuronFrom.getActivatedValue()) * weight) + " from " + neuronFrom.getValue() + " with " + weight);
    }

    public void passError() {
        neuronFrom.addError(weight * neuronTo.getError() * (neuronTo.getActivatedValue() * (1d - neuronTo.getActivatedValue())));
    }

    public void adjustFromError(double learningRate) {
        weight += neuronTo.getError() * (neuronTo.getActivatedValue() * (1d - neuronTo.getActivatedValue())) * neuronFrom.getActivatedValue() * learningRate;
    }

    public void adjustFromError() {
        adjustFromError(1);
    }
}
