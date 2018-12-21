package net.dankrushen.danknn;

import net.dankrushen.danknn.DankImageGrid.DankDrawSpace;
import net.dankrushen.danknn.danklayers.DankInputLayer;
import net.dankrushen.danknn.danklayers.DankLayer;
import net.dankrushen.danknn.danklayers.IDankOutputLayer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class DankNetworkVisualizer {
    public DankImageGrid imageGrid;

    private DankNetwork network;

    public DankNetworkVisualizer(DankNetwork network) {
        this.network = network;
        this.imageGrid = new DankImageGrid();
    }

    public DankNetworkVisualizer(DankNetwork network, int width, int height) {
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

        int connectionRowCount = Math.max(1, getMostConnectionsInLayers(network.getLayers()));

        DankImageGrid connectionImageGrid = imageGrid.clone();

        connectionImageGrid.setRows(connectionRowCount);

        int strokeWidth = 6;
        int ovalSpacing = Math.floorDiv(strokeWidth, 2);

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int x = 0; x < imageGrid.getColumns(); x++) {
            System.out.println();

            for (int y = 0; y < imageGrid.getRows(); y++) {
                DankDrawSpace drawSpace = imageGrid.getDrawSpaceAt(x, y);

                graphics.setClip(drawSpace);

                // Draw neuron layer every even x column
                if (x % 2 == 0) {
                    DankLayer layer = network.getLayers()[x / 2];

                    if (y < layer.getNeurons().length) {
                        System.out.println("Neuron Draw Area: " + drawSpace);

                        DankNeuron neuron = layer.getNeurons()[y];

                        // Neuron Center
                        graphics.setColor(Color.LIGHT_GRAY);
                        graphics.setStroke(new BasicStroke(0));
                        graphics.fillOval(drawSpace.x + ovalSpacing, drawSpace.y + ovalSpacing, drawSpace.width - strokeWidth, drawSpace.height - strokeWidth);

                        // Neuron Outline
                        graphics.setColor(Color.DARK_GRAY);
                        graphics.setStroke(new BasicStroke(strokeWidth));
                        graphics.drawOval(drawSpace.x + ovalSpacing, drawSpace.y + ovalSpacing, drawSpace.width - strokeWidth, drawSpace.height - strokeWidth);

                        DecimalFormat decimalFormat = new DecimalFormat("0.00000");
                        String neuronText = decimalFormat.format(layer instanceof DankInputLayer ? neuron.getValue() : neuron.getActivatedValue());

                        graphics.setFont(graphics.getFont().deriveFont(25f));

                        Rectangle stringBounds = getStringBounds(graphics, neuronText, drawSpace.x, drawSpace.y);

                        graphics.setColor(Color.BLACK);
                        graphics.drawString(neuronText, ((int) Math.floor(drawSpace.getCenterX())) - Math.floorDiv(stringBounds.width, 2), ((int) Math.floor(drawSpace.getCenterY())) + Math.floorDiv(stringBounds.height, 2));
                    }
                }
            }
        }

        graphics.setClip(null);

        graphics.setStroke(new BasicStroke(1));
        graphics.setColor(Color.BLUE);
        connectionImageGrid.drawGridLines(graphics);

        graphics.setStroke(new BasicStroke(1));
        graphics.setColor(Color.RED);
        imageGrid.drawGridLines(graphics);

        graphics.dispose();

        return image;
    }

    private Rectangle getStringBounds(Graphics2D g2, String str, float x, float y) {
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
        return gv.getPixelBounds(null, x, y);
    }

    private int getMostNeuronsInLayers(DankLayer... layers) {
        int highestCount = 0;

        for (DankLayer layer : layers) {
            if (layer.getNeurons().length > highestCount)
                highestCount = layer.getNeurons().length;
        }

        return highestCount;
    }

    private int getMostConnectionsInLayers(DankLayer... layers) {
        int highestCount = 0;

        for (DankLayer layer : layers) {
            if (layer instanceof IDankOutputLayer) {
                IDankOutputLayer outLayer = (IDankOutputLayer) layer;
                if (outLayer.getInputConnections().length > highestCount)
                    highestCount = outLayer.getInputConnections().length;
            }
        }

        return highestCount;
    }
}
