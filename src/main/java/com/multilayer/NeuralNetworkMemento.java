package com.multilayer;

import java.io.*;
import java.util.Scanner;

public class NeuralNetworkMemento implements java.io.Serializable {

    private static final int MAX_VARIANCE = 10;

    // uses the Memento design pattern
    private int inputSize;
    private Layer[] layers;

    private Matrix[] outputHistory;
    private double stepSize = 10; // "eta"

    // extra metadata about the neural net 
    private int epoch;

    public NeuralNetworkMemento(NeuralNetwork network) {

        // while the memento exists, these are "hot" references, even if the NeuralNet discards them
        this.inputSize = network.getInputSize();
        this.layers = network.getLayers();
        this.outputHistory = network.getOutputHistory();
        this.stepSize = network.getStepSize();
        this.epoch = network.getEpoch();
    }

    private NeuralNetworkMemento(){
        this.inputSize = 0;
        this.layers = null;
        this.outputHistory = null;
        this.stepSize = 0;
    }

    public int getInputSize() {
        return this.inputSize;
    }

    public Layer[] getLayers() {
        return this.layers;
    }

    public Matrix[] getOutputHistory() {
        return this.outputHistory;
    }

    public double getStepSize() {
        return this.stepSize;
    }

    public int getEpoch() {
        return this.epoch;
    }

    // original save
    public void saveToFileSerialize(String path) {
        try {

            FileOutputStream file = new FileOutputStream(path);
            ObjectOutputStream output = new ObjectOutputStream(file);

            output.writeObject(this);
            file.close();


        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    // original read from file
    public static NeuralNetworkMemento readFromFileSerialize(String path) {
        try {

            FileInputStream file = new FileInputStream(path);
            ObjectInputStream input = new ObjectInputStream(file);

            NeuralNetworkMemento networkMem = (NeuralNetworkMemento)input.readObject();

            input.close();
            file.close();

            return networkMem;

        } catch (Exception e) {
            System.err.println(e);
        }
        return null;  
    }

    // more "grade friendly" save
    public void saveToFile(String path) {

        try {

            File file = new File(path);
            FileWriter writer = new FileWriter(file);

            // construct a string that contains: 
            // inputSize,stepSize,epoch,depth,layersizes..
            // layer 0 weight0_0, ... ,layer 0 weight0_n
            // ...
            // layer 0 weightm_0, ... ,layer0 weightm_n
            // layer 0 bias_0, ... ,layer 0 bias_n
            // for all layers i < depth

            StringBuilder output = new StringBuilder("");
            int depth = this.getLayers().length;
            int[] layerSizes = new int[depth];

            output.append(this.getInputSize()+","+this.getStepSize()+","+this.getEpoch()+","+this.getLayers().length+",");
            for (int i = 0; i < depth; i++) {
                output.append(this.getLayers()[i].getSize());
                layerSizes[i] = this.getLayers()[i].getSize();
                if (i != depth - 1) {
                    output.append(",");
                }
            }
            output.append("\n");

            // save every matrix for every layer 
            for (int L = 0; L < depth; L++) {

                // weights
                for (int i = 0; i < this.getLayers()[L].getSize(); i++) {
                    for (int j = 0; j < this.getLayers()[L].getWeights().getWidth(); j++) {
                        output.append(this.getLayers()[L].getWeights().getValueAt(i, j));
                        if (j != this.getLayers()[L].getWeights().getWidth() - 1) {
                            output.append(",");
                        }
                    }
                    output.append("\n");
                }
                // output.append("\n");

                // biases
                for (int i = 0; i < this.getLayers()[L].getSize(); i++) {
                    output.append(this.getLayers()[L].getBiases().getValueAt(i, 0));
                    if (i != this.getLayers()[L].getSize() - 1) {
                        output.append(",");
                    }
                }
                output.append("\n");

                

            }

            // Actually you know what who needs output history amirite? 

            // output history needs bigger loop
            // for (int L = 0; L < depth+1; L++) {

            //     // if the network was never run, the history must be set to 0 for all
            //     if (this.getOutputHistory() == null) {
            //         Matrix[] history = new Matrix[depth+1];
            //         history[0] = new Matrix(this.getInputSize(), 1);
            //         for (int i = 1; i <= depth; i++) {
            //             history[i] = new Matrix(layerSizes[i-1], 1);
            //         }
            //     }
            //     // actually add to the string 
            //     for (int i = 0; i < this.getLayers()[L].getSize(); i++) {
            //         output.append(this.getOutputHistory()[L].getValueAt(i, 0));
            //         if (i != layerSizes[L] - 1) {
            //             output.append(",");
            //         }
            //     }
            //     output.append("\n");
            // }

            // finally write the output 
            writer.write(output.toString());
            writer.close();

        } catch (Exception e) {
            System.err.println(e);
        }

    }

    // more "grade friendly" load
    public static NeuralNetworkMemento readFromFile(String path) {

        try {

            // destruct a string that contains: 
            // inputSize,stepSize,epoch,depth,layersizes..
            // layer 0 weight0_0, ... ,layer 0 weight0_n
            // ...
            // layer 0 weightm_0, ... ,layer0 weightm_n
            // layer 0 bias_0, ... ,layer 0 bias_n
            // for all layers i < depth

            File file = new File(path);
            Scanner scan = new Scanner(file);

            // scan and destruct first line 
            String line = scan.nextLine().strip();
            String[] lineOne = line.split(",");

            int inputSize = Integer.parseInt(lineOne[0]);
            double stepSize = Double.parseDouble(lineOne[1]);
            int epoch = Integer.parseInt(lineOne[2]);
            int depth = Integer.parseInt(lineOne[3]);
            int[] layerSizes = new int[depth];
            for (int i = 0; i < depth; i++) {
                layerSizes[i] = Integer.parseInt(lineOne[i+4]);
            }

            // retrieve all the layer data

            Layer[] layers = new Layer[depth];

            // layer 1 is special because we can't use the previous layer to get width data
            Layer layerOne = new Layer(layerSizes[0], inputSize, MAX_VARIANCE);
            // weights 1
            String[] weightStringsOne;
            for (int i = 0; i < layerSizes[0]; i++) {
                line = scan.nextLine().strip();
                weightStringsOne = line.split(",");
                for (int j = 0; j < inputSize; j++) {
                    layerOne.getWeights().setValueAt(Double.parseDouble(weightStringsOne[j]), i, j);
                }
            }

            // biases 1
            String[] biasStringsOne;
            line = scan.nextLine().strip();
            biasStringsOne = line.split(",");
            for (int i = 0; i < layerSizes[0]; i++) {
                layerOne.getBiases().setValueAt(Double.parseDouble(biasStringsOne[i]), i, 0);
            }

            layers[0] = layerOne;

            for (int L = 1; L < depth; L++) {

                // retrieve weights line by line 
                Layer layer = new Layer(layerSizes[L], layerSizes[L-1], MAX_VARIANCE);
                // weights L
                String[] weightStrings;
                for (int i = 0; i < layerSizes[L]; i++) {
                    line = scan.nextLine().strip();
                    weightStrings = line.split(",");
                    for (int j = 0; j < layerSizes[L-1]; j++) {
                        layer.getWeights().setValueAt(Double.parseDouble(weightStrings[j]), i, j);
                    }
                }
                
                // biases L
                String[] biasStrings;
                line = scan.nextLine().strip();
                biasStrings = line.split(",");
                for (int i = 0; i < layerSizes[L]; i++) {
                    layer.getBiases().setValueAt(Double.parseDouble(biasStrings[i]), i, 0);
                }

                layers[L] = layer;

            }

            // finally construct the new network and return
            NeuralNetworkMemento mem = new NeuralNetworkMemento();
            mem.inputSize = inputSize;
            mem.layers = layers;
            mem.outputHistory = new Matrix[depth+1];
            mem.outputHistory[0] = new Matrix(inputSize, 0);
            for (int i = 1; i <= depth; i++) {
                mem.outputHistory[i] = new Matrix(layerSizes[i-1], 0);
            }
            mem.stepSize = stepSize;
            mem.epoch = epoch;

            return mem;

        } catch (Exception e) {
            System.err.println(e);
        }

        return null;

    }
    
}
