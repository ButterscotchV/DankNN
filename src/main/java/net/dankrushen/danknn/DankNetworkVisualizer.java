package net.dankrushen.danknn;

import net.dankrushen.danknn.dankgraphics.DankImageGrid;
import net.dankrushen.danknn.dankgraphics.DankImageGrid.AutoSpacingType;
import net.dankrushen.danknn.danklayers.DankInputLayer;
import net.dankrushen.danknn.danklayers.DankLayer;
import net.dankrushen.danknn.danklayers.IDankOutputLayer;
import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DankNetworkVisualizer {
    public DankImageGrid imageGrid;

    private DankNetwork network;

    private JFrame displayWindow;
    private JLabel imageDisplay;

    public DankNetworkVisualizer(DankNetwork network) {
        this.network = network;
        this.imageGrid = new DankImageGrid();
    }

    public DankNetworkVisualizer(DankNetwork network, int width, int height) {
        this.network = network;
        this.imageGrid = new DankImageGrid(width, height);
    }

    public void setDisplayEnabled(boolean enable) {
        if (enable) {
            if (displayWindow == null)
                displayWindow = new JFrame("Network Visualizer Display");

            if (imageDisplay == null)
                imageDisplay = new JLabel();

            if (!frameContainsComponent(displayWindow, imageDisplay))
                displayWindow.add(imageDisplay);

            displayWindow.pack();
            displayWindow.setVisible(true);
        } else {
            if (displayWindow != null)
                displayWindow.setVisible(false);
        }
    }

    public void setDisplayExitBehaviour(int exitBehaviour) {
        if (displayWindow != null)
            displayWindow.setDefaultCloseOperation(exitBehaviour);
    }

    public void centerDisplay() {
        if (displayWindow != null)
            displayWindow.setLocationRelativeTo(null);
    }

    public void destroyDisplay() {
        if (displayWindow != null) {
            displayWindow.dispose();
            displayWindow = null;
        }

        if (imageDisplay != null)
            imageDisplay = null;
    }

    private boolean frameContainsComponent(JFrame frame, Component component) {
        for (Component frameComponent : displayWindow.getComponents())
            if (frameComponent == component)
                return true;

        return false;
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
        connectionImageGrid.setAutoSpacingType(AutoSpacingType.NONE);

        int strokeWidth = 6;

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        DecimalFormat decimalFormat = new DecimalFormat("0.00000");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        for (int x = 0; x < Math.max(imageGrid.getColumns(), connectionImageGrid.getColumns()); x++) {
            DankLayer layer;
            IDankOutputLayer outputLayer = null;
            boolean neuronLayer = x % 2 == 0;

            if (neuronLayer) {
                layer = network.getLayers()[x / 2];
            } else {
                layer = network.getLayers()[(x + 1) / 2];
                outputLayer = (IDankOutputLayer) layer;
            }

            for (int y = 0; y < Math.max(imageGrid.getRows(), connectionImageGrid.getRows()); y++) {
                // Draw neuron layer every even x column
                if (neuronLayer) {
                    if (y >= imageGrid.getRows())
                        continue;

                    Rectangle drawSpace = imageGrid.getDrawSpaceAt(x, y);
                    graphics.setClip(drawSpace);

                    if (y < layer.getNeurons().length) {
                        // System.out.println("Neuron Draw Area: " + drawSpace); // Spacing debug

                        DankNeuron neuron = layer.getNeurons()[y];

                        drawOvalInternalBorder(graphics, Color.LIGHT_GRAY, Color.DARK_GRAY, strokeWidth, drawSpace);

                        double neuronValue = layer instanceof DankInputLayer ? neuron.getValue() : neuron.getActivatedValue();
                        String neuronText = decimalFormat.format(neuronValue);

                        graphics.setFont(graphics.getFont().deriveFont(25f));

                        Rectangle stringBounds = getStringBounds(graphics, neuronText, drawSpace.x, drawSpace.y);

                        graphics.setColor(Color.BLACK);
                        graphics.drawString(neuronText, ((int) Math.floor(drawSpace.getCenterX())) - Math.floorDiv(stringBounds.width, 2), ((int) Math.floor(drawSpace.getCenterY())) + Math.floorDiv(stringBounds.height, 2));

                        // Draw line to connection
                        graphics.setClip(null);
                        for (int i = 0; i < neuron.getOutputConnections().length; i++) {
                            Rectangle connectionDrawSpace = imageGrid.getDrawSpaceAt(x, y);

                            graphics.setStroke(new BasicStroke(3));
                            graphics.setColor(Color.DARK_GRAY);

                            graphics.setStroke(new BasicStroke(1));
                            graphics.setColor(Color.LIGHT_GRAY);
                        }
                    }
                } else { // Draw connection layer every even x column
                    if (y >= connectionImageGrid.getRows())
                        continue;

                    Rectangle drawSpace = connectionImageGrid.getDrawSpaceAt(x, y);
                    graphics.setClip(drawSpace);

                    if (y < outputLayer.getInputConnections().length) {
                        DankConnection connection = outputLayer.getInputConnections()[y];

                        String neuronText = decimalFormat.format(connection.weight);

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

        if (imageDisplay != null) {
            imageDisplay.setIcon(new ImageIcon(image));

            if (displayWindow != null)
                displayWindow.pack();
        }

        return image;
    }

    private void drawOvalInternalBorder(Graphics2D graphics2D, Color innerColour, Color outerColour, int thickness, int x, int y, int width, int height) {
        int ovalSpacing = Math.floorDiv(thickness, 2);
        boolean antialias = usesAntialiasing(graphics2D);
        int antialiasSpacing = antialias ? 1 : 0;

        // Center
        graphics2D.setColor(innerColour);
        graphics2D.setStroke(new BasicStroke(0));
        graphics2D.fillOval(x + ovalSpacing, y + ovalSpacing, width - thickness - antialiasSpacing, height - thickness - antialiasSpacing);

        // Outline
        graphics2D.setColor(outerColour);
        graphics2D.setStroke(new BasicStroke(thickness));
        graphics2D.drawOval(x + ovalSpacing, y + ovalSpacing, width - thickness - antialiasSpacing, height - thickness - antialiasSpacing);
    }

    private void drawOvalInternalBorder(Graphics2D graphics2D, Color innerColour, Color outerColour, int thickness, Rectangle bounds) {
        drawOvalInternalBorder(graphics2D, innerColour, outerColour, thickness, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private boolean usesAntialiasing(Graphics2D graphics2D) {
        return graphics2D.getRenderingHints().containsKey(RenderingHints.KEY_ANTIALIASING) && graphics2D.getRenderingHints().get(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_ON;
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
