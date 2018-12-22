package net.dankrushen.danknn

import net.dankrushen.danknn.dankgraphics.DankImageGrid
import net.dankrushen.danknn.danklayers.IDankOutputLayer
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame



class DankNN {
    init {
        val networkBuilder = DankNetworkBuilder(2)

        networkBuilder.addLayer(4)
        networkBuilder.addLayer(2)

        val network = networkBuilder.buildNetwork(1)

        val visualizer = DankNetworkVisualizer(network, 1280, 720)

        visualizer.imageGrid.autoSpacingType = DankImageGrid.AutoSpacingType.SQUARE_PLUS_PERCENT
        visualizer.imageGrid.extraAutoSpacingPercent = 15.0

        //saveImage(visualizer.drawImage());
        visualizer.setDisplayEnabled(true)
        visualizer.setDisplayExitBehaviour(JFrame.EXIT_ON_CLOSE)
        visualizer.drawImage()
        visualizer.centerDisplay()

        val trainingSetLength = 100000
        val expectedInOutsList = mutableListOf<Array<DoubleArray>>()

        println("Generating dataset with $trainingSetLength points...")

        val rng = Random()
        for (i in 0 until trainingSetLength) {
            val positive = i >= trainingSetLength / 2
            var matches = false

            while (!matches) {
                val x = rng.nextDouble() * 11 - 5.5
                val y = rng.nextDouble() * 11 - 5.5

                val dist = pythagoreanTheorem(x, y)

                matches = if (positive) dist <= 2.5 else dist >= 3.5

                if (matches) {
                    expectedInOutsList.add(arrayOf(doubleArrayOf(x, y), doubleArrayOf((if (positive) 1 else -1).toDouble())))

                    println((if (positive) "Positive" else "Negative") + " point at (" + x + ", " + y + ")")
                }
            }
        }

        val expectedInOuts = expectedInOutsList.toTypedArray()

        println("Dataset generated!")

        println()

        var loss: Double
        var epochLoss: Double

        var epochs = 0
        var epochIters = 0

        val initTime = System.nanoTime()
        var lastTime = initTime

        val timeDivisor = 1000000000.0

        val learningRate = 0.03

        while (epochs < 1000) {
            epochLoss = 0.0

            for (expectedInOut in shuffleDataset(expectedInOuts)) {
                val outputs = network.forwardprop(expectedInOut[0])
                val errors = DoubleArray(outputs.size)

                for (i in outputs.indices) {
                    errors[i] = expectedInOut[1][i] - outputs[i]
                }

                loss = network.backprop(errors, learningRate)
                epochLoss += loss

                epochIters++

                val curTime = System.nanoTime()
                val deltaTime = curTime - lastTime
                lastTime = curTime

                if (epochIters % 500 == 0 || epochIters >= expectedInOuts.size) {
                    if (false) { // Debugging actual network
                        println()
                        var layerNum = 1
                        for (layer in network.getLayers()) {
                            if (layer is IDankOutputLayer) {
                                print(layerNum.toString() + " to " + (layerNum + 1) + " weights: ")
                                for (connection in (layer as IDankOutputLayer).inputConnections) {
                                    print(connection.weight.toString() + " ")
                                }
                                println()

                                print((layerNum + 1).toString() + " biases: ")
                                for (neuron in layer.neurons) {
                                    print(neuron.bias.toString() + " ")
                                }
                                println()

                                layerNum++
                            }
                        }
                    }

                    println("Iters = " + epochIters + "/" + expectedInOuts.size + ", Epochs = " + epochs + ", Loss = " + loss + ", Time/Iter = " + deltaTime / timeDivisor + ", Total Time = " + (curTime - initTime) / timeDivisor)
                }
            }

            epochs++
            epochLoss /= epochIters.toDouble()
            epochIters = 0

            //learningRate *= 0.985;

            //saveImage(visualizer.drawImage());
            visualizer.drawImage()

            println("Epoch Loss = $epochLoss")
            println()
        }

        val inputs = doubleArrayOf(-0.653557941943574, -2.250244121966053) // Positive
        val outputs = network.forwardprop(inputs)

        println("Point: (" + inputs[0] + ", " + inputs[1] + ")")
        println("Group (+1 or -1): " + outputs[0])
        println()
    }

    private fun pythagoreanTheorem(a: Double, b: Double): Double {
        return Math.sqrt(a * a + b * b)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                //testOnExample();
                //testOnExample2();

                val dankNN = DankNN()

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

            double[] output = network.forwardprop(new double[]{2.5});
            System.out.println(doubleArrayToString(output));
            */
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun testOnExample() {
            val networkBuilder = DankNetworkBuilder(2)

            networkBuilder.addLayer(3)

            val network = networkBuilder.buildNetwork(1)

            val layer1 = network.getLayers()[1] as IDankOutputLayer
            val connections1 = layer1.inputConnections

            connections1[0].weight = 0.8
            connections1[1].weight = 0.4
            connections1[2].weight = 0.3
            connections1[3].weight = 0.2
            connections1[4].weight = 0.9
            connections1[5].weight = 0.5

            val layer2 = network.getLayers()[2] as IDankOutputLayer
            val connections2 = layer2.inputConnections

            connections2[0].weight = 0.3
            connections2[1].weight = 0.5
            connections2[2].weight = 0.9

            var output = network.forwardprop(doubleArrayOf(1.0, 1.0))[0]

            println("Output Num: $output")

            network.backprop(doubleArrayOf(0 - output))

            output = network.forwardprop(doubleArrayOf(1.0, 1.0))[0]

            println("Output Num: $output")
        }

        fun testOnExample2() {
            val networkBuilder = DankNetworkBuilder(2)

            networkBuilder.addLayer(2)

            val network = networkBuilder.buildNetwork(2)

            val layer1 = network.getLayers()[1] as IDankOutputLayer
            val connections1 = layer1.inputConnections

            layer1.setBias(0.35)

            connections1[0].weight = 0.15
            connections1[1].weight = 0.25
            connections1[2].weight = 0.20
            connections1[3].weight = 0.30

            val layer2 = network.getLayers()[2] as IDankOutputLayer
            val connections2 = layer2.inputConnections

            layer2.setBias(0.6)

            connections2[0].weight = 0.40
            connections2[1].weight = 0.50
            connections2[2].weight = 0.45
            connections2[3].weight = 0.55

            var outputs: DoubleArray
            outputs = network.forwardprop(doubleArrayOf(0.05, 0.10))

            println("h1: " + network.getLayers()[1].neurons[0].value)

            println("Output Nums: " + outputs[0] + ", " + outputs[1])

            network.backprop(doubleArrayOf(0.01 - outputs[0], 0.99 - outputs[1]))

            outputs = network.forwardprop(doubleArrayOf(0.05, 0.10))

            println("Output Nums: " + outputs[0] + ", " + outputs[1])

            for (i in 0..9998) {
                outputs = network.forwardprop(doubleArrayOf(0.05, 0.10))

                println("h1: " + network.getLayers()[1].neurons[0].value)

                println("Output Nums: " + outputs[0] + ", " + outputs[1])

                network.backprop(doubleArrayOf(0.01 - outputs[0], 0.99 - outputs[1]))

                println("Loss: " + network.calculateLoss())
            }
        }

        fun doubleArrayToString(array: DoubleArray): String {
            var outputString = ""

            for (i in array.indices) {
                outputString += (if (i == 0) "" else ", ") + array[i]
            }

            return "[$outputString]"
        }

        fun saveImage(image: BufferedImage) {
            try {
                ImageIO.write(image, "png", File("D:\\Documents\\image.png"))
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        fun shuffleDataset(dataset: Array<Array<DoubleArray>>) : Array<Array<DoubleArray>> {
            val dataShuffled = dataset.clone()

            val rng = Random()
            var n = dataShuffled.size
            while (n > 1) {
                n--
                val k = rng.nextInt(n + 1)
                val value = dataShuffled[k]
                dataShuffled[k] = dataShuffled[n]
                dataShuffled[n] = value
            }

            return dataShuffled
        }
    }
}