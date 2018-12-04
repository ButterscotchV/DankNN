package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.DankLayer;
import net.dankrushen.danknn.danklayers.DankInputLayer;
import net.dankrushen.danknn.danklayers.DankOutputLayer;

import java.util.ArrayList;

public class DankNetwork {
    private final ArrayList<DankLayer> layers = new ArrayList<>();

    public DankNetwork(DankLayer... layers) {
        for (DankLayer layer : layers) {
            this.layers.add(layer); // Add each user-defined layer
        }
    }
}
