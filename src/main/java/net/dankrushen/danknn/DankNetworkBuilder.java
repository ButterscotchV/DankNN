package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.*;

import java.util.ArrayList;

public class DankNetworkBuilder {
    private DankNetwork builtNetwork;
    private final ArrayList<DankLayer> layers = new ArrayList<>();

    public DankNetworkBuilder(int inputNeuronCount) {
        layers.add(new DankInputLayer(inputNeuronCount)); // Begin layers
    }

    public void addLayer(int numNeurons) {
        layers.add(new DankFullyConnectedLayer(numNeurons, (IDankInputLayer) layers.get(layers.size() - 1)));
    }

    public DankNetwork buildNetwork(int outputNeuronCount) {
        if (builtNetwork == null) {
            layers.add(new DankOutputLayer(outputNeuronCount, (IDankInputLayer) layers.get(layers.size() - 1)));

            builtNetwork = new DankNetwork(layers.toArray(new DankLayer[] {}));
        }

        return builtNetwork;
    }
}
