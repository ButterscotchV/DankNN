package net.dankrushen.danknn;

import java.text.DecimalFormat;
import java.util.Random;

public class DankNN {
    public static void main(String[] args) {
        try {
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

    public static String doubleArrayToString(double[] array) {
        String outputString = "";

        for (int i = 0; i < array.length; i++) {
            outputString += (i == 0 ? "" : ", ") + array[i];
        }

        return "[" + outputString + "]";
    }

    public DankNN() {
        DankNetworkBuilder networkBuilder = new DankNetworkBuilder(2);

        networkBuilder.addLayer(2);

        DankNetwork network = networkBuilder.buildNetwork(2);

        int trainingSetLength = 1000000;
        double[][][] expectedInOuts = new double[trainingSetLength][][];

        for (int i = 0; i < expectedInOuts.length; i++) {
            double in = (i % (expectedInOuts.length / 2)) + 1;
            boolean multiply = i >= (expectedInOuts.length / 2);

            expectedInOuts[i] = new double[][]{
                    new double[]{in, (multiply ? in * 2d : in / 2d)},
                    new double[]{(multiply ? 1 : 0), (multiply ? 0 : 1)}
            };
        }

        double loss = 1;
        double epochLoss = 1;

        int epochs = 0;
        int epochIters = 0;

        long initTime = System.nanoTime();
        long lastTime = initTime;

        double timeDivisor = 1000000000;

        double learningRate = 1;

        while (epochs < 20) {
            epochLoss = 0;

            for (double[][] expectedInOut : shuffleData(expectedInOuts)) {
                double[] outputs = network.runOnInputs(expectedInOut[0]);
                double[] errors = new double[outputs.length];

                for (int i = 0; i < outputs.length; i++) {
                    errors[i] = expectedInOut[1][i] - outputs[i];
                }

                loss = network.backpropogate(errors, learningRate);
                epochLoss += loss;

                epochIters++;

                long curTime = System.nanoTime();
                long deltaTime = curTime - lastTime;
                lastTime = curTime;

                if (epochIters % 100 == 0 || epochIters >= expectedInOuts.length) {
                    if (false) { // Debugging actual network
                        DecimalFormat f = new DecimalFormat("0.000");
                        String spaces = makeSpaces(5);

                        //System.out.println(f.format(neuron11) + " " + f.format(weight11) + " | " + f.format(neuron21) + " " + f.format(weight31) + " | " + f.format(neuron31));
                        //System.out.println(spaces + "\\" + f.format(weight12) + " | " + spaces + "\\" + f.format(weight32) + " | " + spaces);
                        //System.out.println(spaces + "/" + f.format(weight21) + " | " + spaces + "/" + f.format(weight41) + " | " + spaces);
                        //System.out.println(f.format(neuron12) + " " + f.format(weight22) + " | " + f.format(neuron22) + " " + f.format(weight42) + " | " + f.format(neuron32));
                    }

                    System.out.println("Iters = " + epochIters + "/" + expectedInOuts.length + ", Epochs = " + epochs + ", Loss = " + loss + ", Time/Iter = " + (deltaTime / timeDivisor) + ", Total Time = " + ((curTime - initTime) / timeDivisor));
                }
            }

            epochs++;
            epochLoss /= epochIters;
            epochIters = 0;

            learningRate *= 0.99;

            System.out.println("Epoch Loss = " + epochLoss);
            System.out.println();
        }

        double[] outputs = network.runOnInputs(new double[]{5, 10});

        System.out.println("Divided by 2: " + (outputs[0] * 100) + "%");
        System.out.println("Multiplied by 2: " + (outputs[1] * 100) + "%");
    }

    public static double activation(double netNeuron) {
        return 1d / (1d + Math.exp(-netNeuron));
    }

    public static double lossFunction(double error) {
        return 0.5 * Math.pow(error, 2);
    }

    public static double errorFunction(double predicted, double expected) {
        return expected - predicted;
    }

    public static double weightLoss(double error, double neuronFrom, double neuronTo, double learningRate) {
        return hiddenWeightLoss(error, 1, 1, 0, neuronFrom, neuronTo, learningRate);
    }

    public static double hiddenWeightLoss(double error1, double weight1, double error2, double weight2, double neuronFrom, double neuronTo, double learningRate) {
        return ((-error1 * weight1) + (-error2 * weight2)) * (neuronTo * (1d - neuronTo)) * neuronFrom * learningRate;
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