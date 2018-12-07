package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.DankInputLayer;
import net.dankrushen.danknn.danklayers.DankOutputLayer;

import java.util.Random;

public class DankConnection {
    DankNeuron neuronFrom;
    DankNeuron neuronTo;

    double weight = new Random().nextDouble() - 0.5;

    public DankConnection(DankNeuron neuronFrom, DankNeuron neuronTo, double initWeight) {
        this.neuronFrom = neuronFrom;
        this.neuronTo = neuronTo;
        weight = initWeight;
    }

    public DankConnection(DankNeuron neuronFrom, DankNeuron neuronTo) {
        this.neuronFrom = neuronFrom;
        this.neuronTo = neuronTo;
    }

    public void passError() {
        neuronFrom.addError(calcError() * weight);
    }

    public void passValue() {
        neuronTo.addValue((neuronFrom.getParentLayer() instanceof DankInputLayer ? neuronFrom.getValue() : neuronFrom.getActivatedValue()) * weight);
        //System.out.println("Passed through value of " + ((neuronFrom.getParentLayer() instanceof DankInputLayer ? neuronFrom.getValue() : neuronFrom.getActivatedValue()) * weight) + " from " + neuronFrom.getValue() + " with " + weight);
    }

    public double calcError() {
        return (neuronTo.getParentLayer() instanceof DankOutputLayer ? DankNetwork.derivLossFunction(neuronTo.getError()) : neuronTo.getError()) * DankNetwork.derivActivationFunction(neuronTo.getActivatedValue());
    }

    public void adjustFromError(double learningRate) {
        double deltaTo = calcError();
        weight -= deltaTo * neuronFrom.getActivatedValue() * learningRate;
        neuronTo.subBias(deltaTo * learningRate);
    }

    public void adjustFromError() {
        adjustFromError(1);
    }
}
