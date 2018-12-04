package net.dankrushen.danknn.danklayers;

import net.dankrushen.danknn.DankConnection;

public interface IDankInputLayer extends IDankLayer {
    void setOutputLayer(IDankOutputLayer outputLayer);

    IDankOutputLayer getOutputLayer();

    DankConnection[] getOutputConnections();
}
