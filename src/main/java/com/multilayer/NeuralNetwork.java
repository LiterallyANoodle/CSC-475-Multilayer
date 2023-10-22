package com.multilayer;

public class NeuralNetwork {

    private int DEBUG = 0;
    
    private int inputSize;
    private Layer[] layers;

    private Matrix[] outputHistory;
    private double stepSize = 10; // "eta"

    // instantiate the network to accept input of a certain size 
    // instantiate empty layers from an array which defines their sizes
    public NeuralNetwork(int inputSize, int[] layerSizes) {
        this.inputSize = inputSize;
        this.layers = new Layer[layerSizes.length];
        for (int i = 0; i < layerSizes.length; i++) {
            if (i != 0) {
                this.layers[i] = new Layer(layerSizes[i], layerSizes[i-1]);
                continue;
            }
            this.layers[i] = new Layer(layerSizes[i], inputSize);
        }
    }

    // accessors 

    public int getInputSize() {
        return this.inputSize;
    }

    public Layer[] getLayers() {
        return this.layers;
    }

    // "Layer 0" is the input layer and so is not actually a part of this array of layers
    // Expected input to this accessor is 1 to n
    public Layer getLayer(int i) {
        return this.layers[i-1];
    }

    public int getDepth() {
        return this.layers.length;
    }

    // no mutators for shape because a Neural Network's shape will be immutable once created

    public Matrix[] getOutputHistory() {
        return this.outputHistory;
    }

    public void setOutputHistory(Matrix[] outputHistory) { // TODO make this safe
        this.outputHistory = outputHistory;
    }

    // utilities 

    public String toString() {
        String output = "Network start: \n\n";

        for (int i = 1; i <= this.getDepth(); i++) {
            output += "Layer " + i + "\n";
            output += this.getLayer(i).toString() + "\n\n";
        }
        output += "Network end. \n";

        return output;
    }

    public Matrix forwardPass(Matrix input) {

        try {

            if (input.getHeight() != this.getInputSize()) {
                throw new NeuralNetException("Forward Pass input must be the fixed size accepted by the neural network. This network accepts " + this.getInputSize() + " sized input vectors. Input vector size was " + input.getHeight());
            }
            if (!input.isColumnVector()) {
                throw new NeuralNetException("Forward Pass input must be a column vector.");
            }

            // for each layer of the network:
            // give input, receive output 
            // output becomes input for next layer
            // final layer gives final output 
            // also keep track of intermediate outputs of each layer for later (including input "layer")
            Matrix intermediate = input;
            Matrix[] outputHistory = new Matrix[this.getDepth() + 1];
            outputHistory[0] = new Matrix(input);
            for (int layerIndex = 1; layerIndex <= this.getDepth(); layerIndex++) {
                intermediate = this.getLayer(layerIndex).calculateSigmoidForwardPass(intermediate);
                outputHistory[layerIndex] = intermediate;
            }

            if (DEBUG >= 6) {
                System.out.println("Output history: ");
                for (int i = 0; i < this.getDepth(); i++) {
                    System.out.println(outputHistory[i] + "\n");
                }
                System.out.println();
            }

            this.setOutputHistory(outputHistory);

            return intermediate;

        } catch (NeuralNetException e) {
            System.out.println(e.toString());
        }

        return null;

    }

    public void stochasticGradientDescent(DataPair[] trainingData, int miniBatchSize, int epochNumber) {

        System.out.println("Begin epoch " + epochNumber);

        // randomize training set and divide into equal minibatches 
        // for the toy example, this is just the passed in trainingData in order

        DataPair[] miniBatch1 = new DataPair[2];
        DataPair[] miniBatch2 = new DataPair[2];

        miniBatch1[0] = trainingData[0];
        miniBatch1[1] = trainingData[1];
        miniBatch2[0] = trainingData[2];
        miniBatch2[1] = trainingData[3];

        // just hard coding this for now to quick fix it 
        processMiniBatch(miniBatch1, 0);
        for (int j = 0; j < trainingData.length; j++) {
            System.out.println("Output of minibatch " + 0 + " with Training data " + (j+1) + ": \n" + this.forwardPass(trainingData[j].getInputData()) + "\n");
        }
        processMiniBatch(miniBatch2, 1);
        for (int j = 0; j < trainingData.length; j++) {
            System.out.println("Output of minibatch " + 1 + " with Training data " + (j+1) + ": \n" + this.forwardPass(trainingData[j].getInputData()) + "\n");
        }

        System.out.println("Epoch " + epochNumber + " complete.");
        // System.out.println("Epoch Final Network: \n" + this);
        
    }

    // expects training batch to be a set of pairs of length i
    // each pair is [input, expected output]
    public void processMiniBatch(DataPair[] trainingBatch, int miniBatchNumber) {

        System.out.println("Processing minibatch " + miniBatchNumber + "...");

        if (DEBUG >= 1) {
            System.out.println("Minibatch inputs 0:\n" + trainingBatch[0].getInputData());
            System.out.println("Minibatch inputs 1:\n" + trainingBatch[1].getInputData());
            System.out.println("Minibatch expected output 0:\n" + trainingBatch[0].getExpectedOutput());
            System.out.println("Minibatch expected output 1:\n" + trainingBatch[1].getExpectedOutput());
        }

        // keep a running sum of gradients of each type
        Matrix[] biasGradientSums = new Matrix[this.getDepth()];
        Matrix[] weightGradientSums = new Matrix[this.getDepth()];

        // forward pass with training pair i
        // then grab gradients and sum as you go
        Matrix[][] gradients = null;
        for (int i = 0; i < trainingBatch.length; i++) {
            if (DEBUG >= 2){
                System.out.println("Input vector at i = " + i + "\n" + trainingBatch[i].getInputData());
            }
            this.forwardPass(trainingBatch[i].getInputData());
            gradients = this.calculateTrainingCaseGradients(trainingBatch[i].getExpectedOutput());

            if (DEBUG >= 5) {
                System.out.println("Top bias gradient: \n" + gradients[1][0]);
            }

            for (int l = 1; l <= this.getDepth(); l++) {
                if (biasGradientSums[l-1] == null) {
                    weightGradientSums[l-1] = new Matrix(gradients[0][l-1].getHeight(), gradients[0][l-1].getWidth());
                    biasGradientSums[l-1] = new Matrix(gradients[1][l-1].getHeight(), gradients[1][l-1].getWidth());
                }
                biasGradientSums[l-1] = biasGradientSums[l-1].add(gradients[1][l-1]);
                weightGradientSums[l-1] = weightGradientSums[l-1].add(gradients[0][l-1]);
                if (DEBUG >= 6) {
                    System.out.println("biasGradientSums at i = " + i + " and l = " + l + "\n" + biasGradientSums[l-1]);
                    System.out.println("bias gradients at i = " + i + " and l = " + l + "\n" + gradients[1][l-1]);
                }
            }
        }

        if (DEBUG >= 4) {
            System.out.println("First bias sum: \n" + biasGradientSums[0]);
        }

        // do the rest of the calc and set the new weights and biases 
        for (int l = 1; l <= this.getDepth(); l++) {
            this.getLayer(l).setWeights(this.getLayer(l).getWeights().subtract(weightGradientSums[l-1].scalarMultiply(this.stepSize / trainingBatch.length)));
            this.getLayer(l).setBiases(this.getLayer(l).getBiases().subtract(biasGradientSums[l-1].scalarMultiply(this.stepSize / trainingBatch.length)));
        }

        if (DEBUG >= 4) {
            System.out.println("Ending network: \n" + this);
        }

        System.out.println("Minibatch number " + miniBatchNumber + " revised network:\n" + this);

    }

    // output is [weightGradients[layer], biasGradients[layer]]
    public Matrix[][] calculateTrainingCaseGradients(Matrix expected) {

        // find gradients via backpropagation 
        Matrix[] biasGradients = new Matrix[this.getDepth()];
        Matrix[] weightGradients = new Matrix[this.getDepth()];

        // final layer gradient first 
        // biasGradients[l] is equivalent to this layer's delta vector
        biasGradients[this.getDepth() - 1] = this.getLayer(this.getDepth()).calculateFinalLayerDelta(expected, this.getOutputHistory()[this.getDepth()]);
        weightGradients[this.getDepth() - 1] = this.getLayer(this.getDepth()).calculateWeightGradientFromDelta(biasGradients[this.getDepth() - 1], this.getOutputHistory()[this.getDepth() - 1]);
        for (int l = this.getDepth() - 1; l > 0; l--) {
            biasGradients[l - 1] = this.getLayer(l).calculateHiddenLayerDelta(this.getOutputHistory()[l], biasGradients[l], this.getLayer(l + 1).getWeights());
            weightGradients[l - 1] = this.getLayer(l).calculateWeightGradientFromDelta(biasGradients[l - 1], this.getOutputHistory()[l - 1]);
            if (DEBUG >= 5) {
                System.out.println("biasGradient at l = " + l + "\n" + biasGradients[l-1]);
            }
        }

        return new Matrix[][] {weightGradients, biasGradients};

    }

}
