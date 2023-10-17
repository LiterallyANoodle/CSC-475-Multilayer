package com.multilayer;

import java.lang.Math;

public class Layer {

    // A layer is the smallest unit of the network when nodes are represented as matrices

    private int size;
    private Matrix weights;
    private Matrix biases; 

    public Layer(Matrix weights, Matrix biases) {
        this.setSize(biases.getHeight());
        this.setWeights(weights);
        this.setBiases(biases);
    }

    public Layer(int size, int inputSize) {
        this.setSize(size);
        this.setWeights(new Matrix(size, inputSize));
        this.setBiases(new Matrix(size, 1));
    }

    // accessors and mutators 
    public int getSize() {
        return this.size;
    }

    // size is determined by the matrixes inputted, and therefore cannot be set from outside and must be private
    private void setSize(int size) {
        try {
            if (size < 0) {
                throw new NeuralNetException("Layer size cannot be less than 0.");
            }
            this.size = size;
        } catch (NeuralNetException e) {
            System.out.println(e.toString());
        }
    }

    public Matrix getWeights() {
        return this.weights;
    }

    public void setWeights(Matrix weights) {

        try {
            if (weights.getHeight() != this.getSize()) {
                throw new NeuralNetException("Cannot assign layer weights with matrix of a different height than the layer size.");
            } else {
                this.weights = weights;
            }
        } catch (NeuralNetException e) {
            System.out.println(e.toString());
        }  

    }

    public void setWeightAt(double value, int j, int k) {
        this.getWeights().setValueAt(value, j, k);
    }

    public Matrix getBiases() {
        return this.biases;
    }

    public void setBiases(Matrix biases) {

        try {
            if (biases.getHeight() != this.getSize()) {
                throw new NeuralNetException("Cannot assign layer biases with matrix of a different height than the layer size.");
            } else {
                this.biases = biases;
            }
        } catch (NeuralNetException e) {
            System.out.println(e.toString());
        }  

    }

    public void setBiasAt(double value, int j, int k) {
        this.getBiases().setValueAt(value, j, k);
    }

    // Utilities 

    // This is the function which will run all of the matrix math to obtain Z
    // Z is the intermediate value that is inputted into the activation function on the forward pass
    public Matrix calculateZ(Matrix input) {
        // Z = (weights * input) + bias
        return this.getWeights().matrixMultiply(input).add(this.getBiases());
    }

    public double sigmoid(double z) {
        return (1 / (1 + Math.exp(-z)));
    }

    // run the sigmoid operator over every element in a column vector
    public Matrix sigmoidActivation(Matrix input) {

        Matrix output = new Matrix(input.getHeight(), 1);

        for (int j = 0; j < this.getSize(); j++) {
            output.setValueAt(this.sigmoid(input.getValueAt(j, 0)), j, 0);
        }

        return output;

    }

    // calculate Z and activate in a single method 
    public Matrix calculateSigmoidForwardPass(Matrix input) {
        return this.sigmoidActivation(this.calculateZ(input));
    }

    public String toString() {
        return "Weights: \n" + this.getWeights().toString() + "\n + \nBiases:\n" + this.getBiases().toString();
    }

    // Layer is agnostic to its position in the network
    // that knowledge is only held by the neural network that it is part of 
    public Matrix calculateFinalLayerDelta(Matrix expected, Matrix actual) {

        Matrix deltas = new Matrix(this.getSize(), 1); // column vector for delta on this layer 

        // run equation #1 at each location 
        for (int j = 0; j < deltas.getHeight(); j++) {
            double value = (actual.getValueAt(j, 0) - expected.getValueAt(j, 0)) * actual.getValueAt(j, 0) * (1 - actual.getValueAt(j, 0));
            deltas.setValueAt(value, j, 0);
        }

        return deltas;

    }

    public Matrix calculateHiddenLayerDelta(Matrix actual, Matrix nextDeltas, Matrix nextWeights) {
        
        Matrix deltas = new Matrix(this.getSize(), 1);

        for (int j = 0; j < deltas.getHeight(); j++) {
            double rightHandSide = actual.getValueAt(j, 0) * (1 - actual.getValueAt(j, 0));
            double leftHandSide = nextWeights.getColumn(j).transpose().dotProduct(nextDeltas);

            deltas.setValueAt(leftHandSide * rightHandSide, j, 0);
        }

        return deltas;

    }

    // kind of a waste of memory and time, but doing this for completeness' sake 
    public Matrix calculateBiasGradientFromDelta(Matrix deltas) {
        return new Matrix(deltas);
    }

    public Matrix calculateWeightGradientFromDelta(Matrix deltas, Matrix prevOutput) {

        // Equivalent to DA^T
        
        Matrix prevOutputTranspose = prevOutput.transpose();

        return deltas.matrixMultiply(prevOutputTranspose);

        // Matrix output = new Matrix();

    }
    
}
