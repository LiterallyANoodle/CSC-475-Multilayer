package com.multilayer;

import java.io.*;

public class NeuralNetworkMemento implements java.io.Serializable {

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

    public void saveToFile(String path) {
        try {

            FileOutputStream file = new FileOutputStream(path);
            ObjectOutputStream output = new ObjectOutputStream(file);

            output.writeObject(this);
            file.close();


        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static NeuralNetworkMemento readFromFile(String path) {
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
    
}
