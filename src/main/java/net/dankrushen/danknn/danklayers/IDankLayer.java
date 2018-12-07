package net.dankrushen.danknn.danklayers;

import net.dankrushen.danknn.DankNeuron;

public interface IDankLayer {
    DankNeuron[] getNeurons();
    void setBias(double bias);
}
