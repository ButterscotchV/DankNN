package net.dankrushen.danknn;

import net.dankrushen.danknn.DankImageGrid.DankDrawSpace;
import net.dankrushen.danknn.danklayers.DankLayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DankNetworkVisualizer {
	public DankImageGrid imageGrid;
	
	private DankNetwork network;
	
	public DankNetworkVisualizer (DankNetwork network) {
		this.network = network;
		this.imageGrid = new DankImageGrid();
	}
	
	public DankNetworkVisualizer (DankNetwork network, int width, int height) {
		this.network = network;
		this.imageGrid = new DankImageGrid(width, height);
	}
	
	public BufferedImage drawImage() {
		BufferedImage image = new BufferedImage(imageGrid.getWidth(), imageGrid.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();

		int columnCount = (network.getLayers().length * 2) - 1;
		int rowCount = Math.max(1, getMostNeuronsInLayers(network.getLayers()));

		imageGrid.setColumns(columnCount);
		imageGrid.setRows(rowCount);
		
		graphics.setColor(Color.RED);
		
		for(int x = 0; x < network.getLayers().length; x++) {
			DankLayer layer = network.getLayers()[x];

			for(int y = 0; y < layer.getNeurons().length; y++) {
				DankNeuron neuron = layer.getNeurons()[y];

				DankDrawSpace drawSpace = imageGrid.getDrawSpaceAt(x*2, y);
					
				graphics.fillOval(drawSpace.x, drawSpace.y, drawSpace.width, drawSpace.height);
			}
		}
		
		graphics.dispose();
		
		return image;
	}

	private int getMostNeuronsInLayers(DankLayer... layers) {
		int highestCount = 0;

		for (DankLayer layer : layers) {
			if (layer.getNeurons().length > highestCount)
				highestCount = layer.getNeurons().length;
		}

		return highestCount;
	}
}
