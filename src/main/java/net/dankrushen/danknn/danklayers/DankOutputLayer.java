package net.dankrushen.danknn.danklayers;

import net.dankrushen.danknn.DankConnection;
import net.dankrushen.danknn.DankNeuron;

import java.util.ArrayList;

public class DankOutputLayer extends DankLayer implements IDankOutputLayer {
    private IDankInputLayer inputLayer;
    private DankConnection[] inConnections;

    public DankOutputLayer(int numNeurons, IDankInputLayer inputLayer) {
        super(numNeurons);

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
}
