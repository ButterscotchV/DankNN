package net.dankrushen.danknn.danklayers;

import net.dankrushen.danknn.DankConnection;
import net.dankrushen.danknn.DankNeuron;

import java.util.ArrayList;

public class DankFullyConnectedLayer extends DankLayer implements IDankConnectedLayer, IDankInputLayer, IDankOutputLayer {
    private IDankInputLayer inputLayer;
    private DankConnection[] inConnections;
    private IDankOutputLayer outputLayer;
    private double bias = 0;

    public DankFullyConnectedLayer(int numNeurons, double bias, IDankInputLayer inputLayer) {
        super(numNeurons);

        this.bias = bias;

        init(inputLayer);
    }

    public DankFullyConnectedLayer(int numNeurons, IDankInputLayer inputLayer) {
        super(numNeurons);

        init(inputLayer);
    }

    private void init(IDankInputLayer inputLayer) {
        this.inputLayer = inputLayer;

        inputLayer.setOutputLayer(this);

        ArrayList<DankConnection> inConnectionsList = new ArrayList<>();

        for (DankNeuron externalNeuron : inputLayer.getNeurons()) {
            for (DankNeuron internalNeuron : getNeurons()) {
                inConnectionsList.add(new DankConnection(externalNeuron, internalNeuron));
            }
        }

        inConnections = inConnectionsList.toArray(new DankConnection[]{});
    }

    @Override
    public IDankInputLayer getInputLayer() {
        return inputLayer;
    }

    @Override
    public DankConnection[] getInputConnections() {
        return inConnections;
    }

    @Override
    public void setOutputLayer(IDankOutputLayer outputLayer) {
        this.outputLayer = outputLayer;
    }

    @Override
    public IDankOutputLayer getOutputLayer() {
        return outputLayer;
    }

    @Override
    public DankConnection[] getOutputConnections() {
        return outputLayer.getInputConnections();
    }

    @Override
    public double getBias() {
        return bias;
    }
}
