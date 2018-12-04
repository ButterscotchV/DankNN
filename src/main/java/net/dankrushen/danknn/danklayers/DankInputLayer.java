package net.dankrushen.danknn.danklayers;

import net.dankrushen.danknn.DankConnection;

public class DankInputLayer extends DankLayer implements IDankInputLayer {
    private IDankOutputLayer outputLayer;

    public DankInputLayer(int numNeurons) {
        super(numNeurons);
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
}
