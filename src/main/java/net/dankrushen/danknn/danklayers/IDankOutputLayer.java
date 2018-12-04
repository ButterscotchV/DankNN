package net.dankrushen.danknn.danklayers;

import net.dankrushen.danknn.DankConnection;

public interface IDankOutputLayer extends IDankLayer {
    IDankInputLayer getInputLayer();
    DankConnection[] getInputConnections();
}
