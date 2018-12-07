package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.DankLayer;

public class DankNeuron {
    private final DankLayer parentLayer;

    private double value;
    private double activatedValue;
    private double bias;
    private double error;
    private int iters;

    public DankNeuron(DankLayer parentLayer) {
        this.parentLayer = parentLayer;

        reset();
    }

    public DankLayer getParentLayer() {
        return parentLayer;
    }

    public void reset() {
        value = 0;
        bias = 0;
        error = 0;
        iters = 0;
    }

    public double getValue() {
        return value + bias;
    }

    public double getActivatedValue() {
        return activatedValue;
    }

    public void setValue(double value) {
        this.value = value;
        this.activatedValue = DankNetwork.activationFunction(getValue());
    }

    public void addValue(double value) {
        setValue(this.value + value);
    }

    public void subValue(double value) {
        setValue(this.value - value);
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public void addBias(double bias) {
        this.bias += bias;
    }

    public void subBias(double bias) {
        this.bias -= bias;
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

    public int getIters() {
        return iters;
    }

    public void setIters(int iters) {
        this.iters = iters;
    }

    public void addIters(double iters) {
        this.iters += iters;
    }

    public void subIters(double iters) {
        this.iters -= iters;
    }
}
