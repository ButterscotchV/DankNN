package net.dankrushen.danknn;

import java.text.DecimalFormat;
import java.util.Random;

public class DankNN {
    public static void main (String[] args) {
        try {
            DankNN dankNN = new DankNN();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DankNN() {
        double weight11 = 1; // neuron11 -> neuron21
        double weight12 = 1; // neuron11 -> neuron22

        double weight21 = 1; // neuron12 -> neuron21
        double weight22 = 1; // neuron12 -> neuron22

        double weight31 = 1; // neuron21 -> neuron31
        double weight32 = 1; // neuron21 -> neuron32

        double weight41 = 1; // neuron22 -> neuron31
        double weight42 = 1; // neuron22 -> neuron32

        double neuron11 = 4;
        double neuron12 = 2;

        double neuron21;
        double neuron22;

        double neuron31;
        double neuron32;

        int trainingSetLength = 1000000;
        double[][][] expectedInOut = new double[trainingSetLength][][];

        for (int i = 0; i < expectedInOut.length; i++) {
            double in = (i % (expectedInOut.length / 2)) + 1;
            boolean multiply = i >= (expectedInOut.length / 2);

            expectedInOut[i] = new double[][] {
                    new double[] {in, (multiply ? in * 2d : in / 2d)},
                    new double[] {(multiply ? 1 : 0), (multiply ? 0 : 1)}
            };
        }

        double loss = 1;
        double epochLoss = 1;
        double error1;
        double error2;

        int iters = 0;
        int epochs = 0;
        int epochIters = 0;

        long initTime = System.nanoTime();
        long lastTime = initTime;

        double timeDivisor = 1000000000;

        double learningRate = 1;

        while(epochs < 10) {
            epochLoss = 0;

            for (double[][] expectedOuts : shuffleData(expectedInOut)) {
                double expectedOut1 = expectedOuts[1][0];
                double expectedOut2 = expectedOuts[1][1];

                neuron11 = expectedOuts[0][0];
                neuron12 = expectedOuts[0][1];

                neuron21 = activation((neuron11 * weight11) + (neuron12 * weight21));
                neuron22 = activation((neuron11 * weight12) + (neuron12 * weight22));

                neuron31 = activation((neuron21 * weight31) + (neuron22 * weight41));
                neuron32 = activation((neuron21 * weight32) + (neuron22 * weight42));

                error1 = errorFunction(neuron31, expectedOut1);
                error2 = errorFunction(neuron32, expectedOut2);
                loss = lossFunction(error1) + lossFunction(error2);
                epochLoss += loss;

                weight11 -= hiddenWeightLoss(error1, weight31, error2, weight32, neuron11, neuron21, learningRate); // neuron11 -> neuron21
                weight12 -= hiddenWeightLoss(error1, weight41, error2, weight42, neuron11, neuron22, learningRate); // neuron11 -> neuron22

                weight21 -= hiddenWeightLoss(error1, weight31, error2, weight32, neuron12, neuron21, learningRate); // neuron12 -> neuron21
                weight22 -= hiddenWeightLoss(error1, weight41, error2, weight42, neuron12, neuron22, learningRate); // neuron12 -> neuron22

                weight31 -= weightLoss(error1, neuron21, neuron31, learningRate); // neuron21 -> neuron31
                weight32 -= weightLoss(error2, neuron21, neuron32, learningRate); // neuron21 -> neuron32

                weight41 -= weightLoss(error1, neuron22, neuron31, learningRate); // neuron22 -> neuron31
                weight42 -= weightLoss(error2, neuron22, neuron32, learningRate); // neuron22 -> neuron32

                iters++;
                epochIters++;

                long curTime = System.nanoTime();
                long deltaTime = curTime - lastTime;
                lastTime = curTime;

                if (iters % 100 == 0) {
                    if (true) { // Debugging actual network
                        DecimalFormat f = new DecimalFormat("0.000");
                        String spaces = makeSpaces(5);

                        System.out.println(f.format(neuron11) + " " + f.format(weight11) + " | " + f.format(neuron21) + " " + f.format(weight31) + " | " + f.format(neuron31));
                        System.out.println(spaces + "\\" + f.format(weight12) + " | " + spaces + "\\" + f.format(weight32) + " | " + spaces);
                        System.out.println(spaces + "/" + f.format(weight21) + " | " + spaces + "/" + f.format(weight41) + " | " + spaces);
                        System.out.println(f.format(neuron12) + " " + f.format(weight22) + " | " + f.format(neuron22) + " " + f.format(weight42) + " | " + f.format(neuron32));
                    }

                    System.out.println("Iters = " + epochIters + "/" + expectedInOut.length + ", Epochs = " + epochs + ", Loss = " + loss + ", Time/Iter = " + (deltaTime / timeDivisor) + ", Total Time = " + ((curTime - initTime) / timeDivisor));
                }
            }

            epochs++;
            epochLoss /= epochIters;
            epochIters = 0;

            learningRate *= 0.975;

            System.out.println("Epoch Loss = " + epochLoss);
            System.out.println();
        }

        neuron11 = 2;
        neuron12 = 1;

        neuron21 = activation((neuron11 * weight11) + (neuron12 * weight21));
        neuron22 = activation((neuron11 * weight12) + (neuron12 * weight22));

        neuron31 = activation((neuron21 * weight31) + (neuron22 * weight41));
        neuron32 = activation((neuron21 * weight32) + (neuron22 * weight42));

        System.out.println("Divided by 2: " + (neuron31 * 100) + "%");
        System.out.println("Multiplied by 2: " + (neuron32 * 100) + "%");
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
        while (n > 1)
        {
            n--;
            int k = rng.nextInt(n + 1);
            double[][] value = dataShuffled[k];
            dataShuffled[k] = dataShuffled[n];
            dataShuffled[n] = value;
        }

        return dataShuffled;
    }
}