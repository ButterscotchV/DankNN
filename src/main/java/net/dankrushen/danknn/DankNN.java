package net.dankrushen.danknn;

import net.dankrushen.danknn.danklayers.DankLayer;
import net.dankrushen.danknn.danklayers.IDankOutputLayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

public class DankNN {
    public static void main(String[] args) {
        try {
            //testOnExample();
            //testOnExample2();

            DankNN dankNN = new DankNN();

            /*
            DankNetworkBuilder builder = new DankNetworkBuilder(1);

            builder.addLayer(1);

            DankNetwork network = builder.buildNetwork(1);

        	//int connections = 0;
        	
        	//for (DankLayer layer : network.getLayers()) {
        	//	if (layer instanceof IDankInputLayer)
        	//		connections += ((IDankInputLayer)layer).getOutputConnections().length;
        	//}
        	
            //System.out.println("There are " + connections + " connections in the network.");

            double[] output = network.runOnInputs(new double[]{2.5});
            System.out.println(doubleArrayToString(output));
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testOnExample() {
        DankNetworkBuilder networkBuilder = new DankNetworkBuilder(2);

        networkBuilder.addLayer(3);

        DankNetwork network = networkBuilder.buildNetwork(1);

        IDankOutputLayer layer1 = (IDankOutputLayer) network.getLayers()[1];
        DankConnection[] connections1 = layer1.getInputConnections();

        connections1[0].weight = 0.8;
        connections1[1].weight = 0.4;
        connections1[2].weight = 0.3;
        connections1[3].weight = 0.2;
        connections1[4].weight = 0.9;
        connections1[5].weight = 0.5;

        IDankOutputLayer layer2 = (IDankOutputLayer) network.getLayers()[2];
        DankConnection[] connections2 = layer2.getInputConnections();

        connections2[0].weight = 0.3;
        connections2[1].weight = 0.5;
        connections2[2].weight = 0.9;

        double output = network.runOnInputs(new double[]{1, 1})[0];

        System.out.println("Output Num: " + output);

        network.backpropagate(new double[]{0 - output});

        output = network.runOnInputs(new double[]{1, 1})[0];

        System.out.println("Output Num: " + output);
    }

    public static void testOnExample2() {
        DankNetworkBuilder networkBuilder = new DankNetworkBuilder(2);

        networkBuilder.addLayer(2);

        DankNetwork network = networkBuilder.buildNetwork(2);

        IDankOutputLayer layer1 = (IDankOutputLayer) network.getLayers()[1];
        DankConnection[] connections1 = layer1.getInputConnections();

        layer1.setBias(0.35);

        connections1[0].weight = 0.15;
        connections1[1].weight = 0.25;
        connections1[2].weight = 0.20;
        connections1[3].weight = 0.30;

        IDankOutputLayer layer2 = (IDankOutputLayer) network.getLayers()[2];
        DankConnection[] connections2 = layer2.getInputConnections();

        layer2.setBias(0.6);

        connections2[0].weight = 0.40;
        connections2[1].weight = 0.50;
        connections2[2].weight = 0.45;
        connections2[3].weight = 0.55;

        double[] outputs;
        outputs = network.runOnInputs(new double[]{0.05, 0.10});

        System.out.println("h1: " + network.getLayers()[1].getNeurons()[0].getValue());

        System.out.println("Output Nums: " + outputs[0] + ", " + outputs[1]);

        network.backpropagate(new double[]{0.01 - outputs[0], 0.99 - outputs[1]});

        outputs = network.runOnInputs(new double[]{0.05, 0.10});

        System.out.println("Output Nums: " + outputs[0] + ", " + outputs[1]);

        for (int i = 0; i < 9999; i++) {
            outputs = network.runOnInputs(new double[]{0.05, 0.10});

            System.out.println("h1: " + network.getLayers()[1].getNeurons()[0].getValue());

            System.out.println("Output Nums: " + outputs[0] + ", " + outputs[1]);

            network.backpropagate(new double[]{0.01 - outputs[0], 0.99 - outputs[1]});

            System.out.println("Loss: " + network.calculateLoss());
        }
    }

    public static String doubleArrayToString(double[] array) {
        String outputString = "";

        for (int i = 0; i < array.length; i++) {
            outputString += (i == 0 ? "" : ", ") + array[i];
        }

        return "[" + outputString + "]";
    }

    public static void saveImage(BufferedImage image) {
        try {
            ImageIO.write(image, "png", new File("D:\\Documents\\image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DankNN() {
        DankNetworkBuilder networkBuilder = new DankNetworkBuilder(2);

        networkBuilder.addLayer(4);
        networkBuilder.addLayer(2);

        DankNetwork network = networkBuilder.buildNetwork(1);

        DankNetworkVisualizer visualizer = new DankNetworkVisualizer(network, 1280, 720);

        visualizer.imageGrid.setAutoSpacingType(DankImageGrid.AutoSpacingType.SQUARE_PLUS_PERCENT);
        visualizer.imageGrid.setExtraAutoSpacingPercent(15);

        saveImage(visualizer.drawImage());

        int trainingSetLength = 100000;
        double[][][] expectedInOuts = new double[trainingSetLength][][];

        System.out.println("Generating dataset with " + trainingSetLength + " points...");

        Random rng = new Random();
        for (int i = 0; i < expectedInOuts.length; i++) {
            boolean positive = i >= (expectedInOuts.length / 2);
            boolean matches = false;

            while (!matches) {
                double x = (rng.nextDouble() * 11) - 5.5;
                double y = (rng.nextDouble() * 11) - 5.5;

                double dist = pythagoreanTheorum(x, y);

                matches = (positive ? dist <= 2.5 : dist >= 3.5);

                if (matches) {
                    expectedInOuts[i] = new double[][]{
                            new double[]{x, y},
                            new double[]{positive ? 1 : -1}
                    };

                    System.out.println((positive ? "Positive" : "Negative") + " point at (" + x + ", " + y + ")");
                }
            }
        }

        System.out.println("Dataset generated!");

        System.out.println();

        double loss = 1;
        double epochLoss = 1;

        int epochs = 0;
        int epochIters = 0;

        long initTime = System.nanoTime();
        long lastTime = initTime;

        double timeDivisor = 1000000000;

        double learningRate = 0.03;

        while (epochs < 1000) {
            epochLoss = 0;

            for (double[][] expectedInOut : shuffleData(expectedInOuts)) {
                double[] outputs = network.runOnInputs(expectedInOut[0]);
                double[] errors = new double[outputs.length];

                for (int i = 0; i < outputs.length; i++) {
                    errors[i] = expectedInOut[1][i] - outputs[i];
                }

                loss = network.backpropagate(errors, learningRate);
                epochLoss += loss;

                epochIters++;

                long curTime = System.nanoTime();
                long deltaTime = curTime - lastTime;
                lastTime = curTime;

                if (epochIters % 500 == 0 || epochIters >= expectedInOuts.length) {
                    if (true) { // Debugging actual network
                        DecimalFormat f = new DecimalFormat("0.000");
                        String spaces = makeSpaces(5);

                        //System.out.println(f.format(neuron11) + " " + f.format(weight11) + " | " + f.format(neuron21) + " " + f.format(weight31) + " | " + f.format(neuron31));
                        //System.out.println(spaces + "\\" + f.format(weight12) + " | " + spaces + "\\" + f.format(weight32) + " | " + spaces);
                        //System.out.println(spaces + "/" + f.format(weight21) + " | " + spaces + "/" + f.format(weight41) + " | " + spaces);
                        //System.out.println(f.format(neuron12) + " " + f.format(weight22) + " | " + f.format(neuron22) + " " + f.format(weight42) + " | " + f.format(neuron32));

                        System.out.println();
                        int layerNum = 1;
                        for (DankLayer layer : network.getLayers()) {
                            if (layer instanceof IDankOutputLayer) {
                                System.out.print(layerNum + " to " + (layerNum + 1) + " weights: ");
                                for (DankConnection connection : ((IDankOutputLayer) layer).getInputConnections()) {
                                    System.out.print(connection.weight + " ");
                                }
                                System.out.println();

                                System.out.print((layerNum + 1) + " biases: ");
                                for (DankNeuron neuron : layer.getNeurons()) {
                                    System.out.print(neuron.getBias() + " ");
                                }
                                System.out.println();

                                layerNum++;
                            }
                        }
                    }

                    saveImage(visualizer.drawImage());

                    System.exit(0);

                    System.out.println("Iters = " + epochIters + "/" + expectedInOuts.length + ", Epochs = " + epochs + ", Loss = " + loss + ", Time/Iter = " + (deltaTime / timeDivisor) + ", Total Time = " + ((curTime - initTime) / timeDivisor));
                }
            }

            epochs++;
            epochLoss /= epochIters;
            epochIters = 0;

            //learningRate *= 0.985;

            System.out.println("Epoch Loss = " + epochLoss);
            System.out.println();
        }

        double[] inputs = new double[]{-0.6584706016718167, -0.17883548346504163};
        double[] outputs = network.runOnInputs(inputs);

        System.out.println("Point: (" + inputs[0] + ", " + inputs[1] + ")");
        System.out.println("Group (+1 or -1): " + outputs[0]);
        System.out.println();
    }

    public double pythagoreanTheorum(double a, double b) {
        return Math.sqrt((a * a) + (b * b));
    }

    public static String makeSpaces(int spaces) {
        String spacesString = "";

        for (int i = 0; i < spaces; i++) {
            spacesString += " ";
        }

        return spacesString;
    }

    public static double[][][] shuffleData(double[][]... data) {
        double[][][] dataShuffled = data.clone();

        Random rng = new Random();
        int n = dataShuffled.length;
        while (n > 1) {
            n--;
            int k = rng.nextInt(n + 1);
            double[][] value = dataShuffled[k];
            dataShuffled[k] = dataShuffled[n];
            dataShuffled[n] = value;
        }

        return dataShuffled;
    }
}