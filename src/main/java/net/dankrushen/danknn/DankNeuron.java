package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.DankLayer;
import net.dankrushen.danknn.danklayers.IDankOutputLayer;

public class DankNeuron {
    private final DankLayer parentLayer;

    private double value;
    private double activatedValue;
    private double error;

    public DankNeuron(DankLayer parentLayer) {
        this.parentLayer = parentLayer;

        reset();
    }

    public DankLayer getParentLayer() {
        return parentLayer;
    }

    public void reset() {
        value = 0;
        error = 0;
    }

    public double getValue() {
        return value + getBias();
    }

    public double getActivatedValue() {
        return activatedValue;
    }

    public void setValue(double value) {
        this.value = value;
        this.activatedValue = DankNetwork.activationFunction(value + getBias());
    }

    public void addValue(double value) {
        setValue(this.value + value);
    }

    public void subValue(double value) {
        setValue(this.value - value);
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

    public double getBias() {
        return parentLayer instanceof IDankOutputLayer ? ((IDankOutputLayer) parentLayer).getBias() : 0;
    }
}
