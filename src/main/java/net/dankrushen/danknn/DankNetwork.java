package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.DankLayer;
import net.dankrushen.danknn.danklayers.IDankInputLayer;
import net.dankrushen.danknn.danklayers.IDankOutputLayer;

import java.util.ArrayList;

public class DankNetwork {
    private final ArrayList<DankLayer> layers = new ArrayList<>();
    public double learningRate = 1;

    public DankNetwork(DankLayer... layers) {
        for (DankLayer layer : layers) {
            this.layers.add(layer); // Add each user-defined layer
        }
    }

    public DankLayer[] getLayers() {
        return layers.toArray(new DankLayer[]{});
    }

    public IDankInputLayer getInputLayer() {
        return (IDankInputLayer) layers.get(0);
    }

    public IDankOutputLayer getOutputLayer() {
        return (IDankOutputLayer) layers.get(layers.size() - 1);
    }

    public void resetAllNeurons() {
        // Loop through every layer & set neuron values to 0
        for (DankLayer layer : layers) {
            for (DankNeuron neuron : layer.getNeurons()) {
                neuron.reset();
            }
        }
    }

    public void resetAllNeuronErrors() {
        // Loop through every layer & set neuron error to 0
        for (DankLayer layer : layers) {
            for (DankNeuron neuron : layer.getNeurons()) {
                neuron.setError(0);
            }
        }
    }

    public double[] runOnInputs(double[] inputs) {
        resetAllNeurons(); // Reset all neurons to 0

        DankNeuron[] inputNeurons = getInputLayer().getNeurons();

        // Set input layer values
        for (int i = 0; i < inputNeurons.length; i++) {
            inputNeurons[i].setValue(inputs[i]);
        }

        // Loop through every layer past the input layer & calculate neuron values
        for (int i = 1; i < layers.size(); i++) {
            for (DankConnection connection : ((IDankOutputLayer) layers.get(i)).getInputConnections()) {
                connection.passValue();
            }
        }

        // Get output layer
        DankNeuron[] outputNeurons = getOutputLayer().getNeurons();
        double[] outputs = new double[outputNeurons.length];

        // Fill outputs values
        for (int i = 0; i < outputNeurons.length; i++) {
            outputs[i] = outputNeurons[i].getActivatedValue();
        }

        return outputs;
    }

    public double backpropogate(double[] errors, double learningRate) {
        resetAllNeuronErrors(); // Reset all errors to 0 ()

        DankNeuron[] outputNeurons = getOutputLayer().getNeurons();

        // Set output layer errors
        for (int i = 0; i < outputNeurons.length; i++) {
            outputNeurons[i].setError(errors[i]);
        }

        // Loop backwards through every layer except the input layer & calculate neuron errors
        for (int i = layers.size() - 1; i > 0; i--) {
            for (DankConnection connection : ((IDankOutputLayer) layers.get(i)).getInputConnections()) {
                connection.passError();
                connection.adjustFromError(learningRate);
            }
        }

        return calculateLoss();
    }

    public double backpropogate(double[] errors) {
        return backpropogate(errors, learningRate);
    }

    public double calculateLoss() {
        double loss = 0;

        DankNeuron[] outputNeurons = getOutputLayer().getNeurons();

        // Set output layer errors
        for (int i = 0; i < outputNeurons.length; i++) {
            loss += lossFunction(outputNeurons[i].getError());
        }

        return loss;
    }

    public static double activationFunction(double neuronVal) {
        return 1d / (1d + Math.exp(-neuronVal));
    }

    public static double lossFunction(double errorVal) {
        return (errorVal * errorVal) / 2d;
    }
}
