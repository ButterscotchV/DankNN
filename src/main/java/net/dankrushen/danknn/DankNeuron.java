package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.DankLayer;

public class DankNeuron {
    private final DankLayer parentLayer;

    private double value;
    private double error;

    public DankNeuron(DankLayer parentLayer) {
        this.parentLayer = parentLayer;

        reset();
    }

    public DankNeuron(DankLayer parentLayer, double value) {
        this.parentLayer = parentLayer;

        reset();
        this.value = value;
    }

    public DankLayer getParentLayer() {
        return parentLayer;
    }

    public void reset() {
        value = 0;
        error = 0;
    }

    public double getValue() {
        return value;
    }

    public double getActivatedValue() {
        return DankNetwork.activationFunction(value);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void addValue(double value) {
        this.value += value;
    }

    public void subValue(double value) {
        this.value -= value;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public void addError(double error) {
        this.error += error;
    }

    public void subError(double error) {
        this.error -= error;
    }
}
