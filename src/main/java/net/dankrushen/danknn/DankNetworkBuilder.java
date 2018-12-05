package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.*;

import java.util.ArrayList;

public class DankNetworkBuilder {
    private DankNetwork builtNetwork;
    private final ArrayList<DankLayer> layers = new ArrayList<>();

    public DankNetworkBuilder(int inputNeuronCount) {
        layers.add(new DankInputLayer(inputNeuronCount)); // Begin layers
    }

    public void addLayer(int numNeurons, double bias) {
        layers.add(new DankFullyConnectedLayer(numNeurons, bias, (IDankInputLayer) layers.get(layers.size() - 1)));
    }

    public void addLayer(int numNeurons) {
        addLayer(numNeurons, 0);
    }

    public DankNetwork buildNetwork(int outputNeuronCount, double bias) {
        if (builtNetwork == null) {
            layers.add(new DankOutputLayer(outputNeuronCount, bias, (IDankInputLayer) layers.get(layers.size() - 1)));

            builtNetwork = new DankNetwork(layers.toArray(new DankLayer[]{}));
        }

        return builtNetwork;
    }

    public DankNetwork buildNetwork(int outputNeuronCount) {
        return buildNetwork(outputNeuronCount, 0);
    }
}
