package net.dankrushen.danknn.danklayers;

import net.dankrushen.danknn.DankNeuron;

public abstract class DankLayer implements IDankLayer {
    private DankNeuron[] neurons;

    public DankLayer(int numNeurons) {
        neurons = new DankNeuron[numNeurons];

        for (int i = 0; i < numNeurons; i++) {
            neurons[i] = new DankNeuron(this);
        }
    }

    @Override
    public DankNeuron[] getNeurons() {
        return neurons;
    }

    @Override
    public void setBias(double bias) {
        for (DankNeuron neuron : getNeurons()) {
            neuron.setBias(bias);
        }
    }
}
