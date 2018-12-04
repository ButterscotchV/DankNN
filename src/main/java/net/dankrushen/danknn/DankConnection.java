package net.dankrushen.danknn;

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
}
