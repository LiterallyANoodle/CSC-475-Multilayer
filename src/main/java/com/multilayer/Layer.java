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

    // size is determined by the matrixes inputted, and therefore cannot be set from outside
    private void setSize(int size) {
        this.size = size;
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
    
}
