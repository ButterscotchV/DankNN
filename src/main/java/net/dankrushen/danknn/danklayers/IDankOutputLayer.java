package net.dankrushen.danknn.danklayers;

import net.dankrushen.danknn.DankConnection;
import net.dankrushen.danknn.DankNeuron;

public interface IDankOutputLayer extends IDankLayer {
    IDankInputLayer getInputLayer();

    DankConnection[] getInputConnections();
}
