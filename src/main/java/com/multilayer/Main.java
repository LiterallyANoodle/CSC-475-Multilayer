package com.multilayer;

import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;

class Main {

    public static void main(String[] args) {

        // Standardized matrices from assignment pdf: 
        Matrix L1TestWeights = new Matrix(new double[][] {{-0.21, 0.72, -0.25, 1}, 
                                                         {-0.94, -0.41, -0.47, 0.63},
                                                         {0.15, 0.55, -0.49, -0.75}});
        Matrix L1TestBiases = new Matrix(new double[][] {{0.1},
                                                        {-0.36},
                                                        {-0.31}});
        Matrix L2TestWeights = new Matrix(new double[][] {{0.76, 0.48, -0.73},
                                                         {0.34, 0.89, -0.23}});
        Matrix L2TestBiases = new Matrix(new double[][] {{0.16},
                                                        {-0.46}});

        double testLearningRate = 10f;

        // print assignment matrices 
        System.out.println("Assignment matrices ----------");
        System.out.println(L1TestWeights.toString() + "\n");
        System.out.println(L1TestBiases.toString() + "\n");
        System.out.println(L2TestWeights.toString() + "\n");
        System.out.println(L2TestBiases.toString() + "\n");

        // matrixTesting();
        // layerTesting();

        // test with some data 
        Matrix testInput1 = new Matrix(new double[][] {{0},
                                                      {1},
                                                      {0},
                                                      {1}});

        NeuralNetwork testNet = new NeuralNetwork(4, new int[] {3, 2});

        testNet.getLayer(1).setWeights(L1TestWeights);
        testNet.getLayer(1).setBiases(L1TestBiases);
        testNet.getLayer(2).setWeights(L2TestWeights);
        testNet.getLayer(2).setBiases(L2TestBiases);

        System.out.println(testNet.toString());

        Matrix testResult1 = testNet.forwardPass(testInput1);
        System.out.println("Test Foward Pass Result: \n" + testResult1);

        // training set for toy network: 
        // array of 4 training pairs 
        // each pair contains the input vector and expected output vector 
        Matrix[][] trainingData = new Matrix[4][2];

        // input vector 1
        trainingData[0][0] = new Matrix(new double[][] {{0},
                                                        {1},
                                                        {0},
                                                        {1}});
        // output vector 1
        trainingData[0][1] = new Matrix(new double[][] {{0},
                                                        {1}});

        // input vector 2
        trainingData[1][0] = new Matrix(new double[][] {{1},
                                                        {0},
                                                        {1},
                                                        {0}});
        // output vector 2
        trainingData[1][1] = new Matrix(new double[][] {{1},
                                                        {0}});

        // input vector 3
        trainingData[2][0] = new Matrix(new double[][] {{0},
                                                        {0},
                                                        {1},
                                                        {1}});
        // output vector 3
        trainingData[2][1] = new Matrix(new double[][] {{0},
                                                        {1}});

        // input vector 4
        trainingData[3][0] = new Matrix(new double[][] {{1},
                                                        {1},
                                                        {0},
                                                        {0}});
        // output vector 4
        trainingData[3][1] = new Matrix(new double[][] {{1},
                                                        {0}});

        Matrix[][] testMinibatch = new Matrix[2][2];

        testMinibatch[0] = trainingData[0];
        testMinibatch[1] = trainingData[1];

        testNet.processMiniBatch(testMinibatch);

    }

    private static void layerTesting() {

        Matrix weights = new Matrix(new double[][] {{1, 2},
                                                   {3, 4}});

        Matrix biases = new Matrix(new double[][] {{2},
                                                  {4}});

        Matrix input = new Matrix(new double[][] {{1},
                                                   {3}});

        Layer L1 = new Layer(weights, biases);

        Matrix Z1 = L1.calculateZ(input);

        Matrix A1 = L1.sigmoidActivation(Z1);

        System.out.println(Z1.toString() + "\n");
        System.out.println(A1.toString() + "\n");

    }

    private static void matrixTesting() {

        // basic test matrices
        Matrix testM1 = new Matrix(3, 3);
        Matrix testM2 = new Matrix(new double[][] {{1, 2, 3},
                                                  {4, 5, 6},
                                                  {7, 8, 9}});
        Matrix testM3 = new Matrix(new double[][] {{1, 2},
                                                  {3, 4}});

        // print basic operations 
        System.out.println("Basic matrices ----------");
        System.out.println(testM1.add(testM2) + "\n");
        System.out.println(testM2.getRow(1).toString() + "\n");
        System.out.println(testM2.getColumn(1).toString() + "\n");
        try {
            System.out.println(testM2.getRow(1).dotProduct(testM2.getColumn(1)) + "\n");
        } catch (MatrixException e) {
            System.out.println(e.toString());
        }
        System.out.println(testM2.matrixMultiply(testM2) + "\n");
        System.out.println(testM3.matrixMultiply(testM3) + "\n");

        // test speed with large matrices 
        Matrix bigMatrix = new Matrix(1000, 1000);

        long startTime = System.nanoTime();
        for (int i = 0; i < bigMatrix.getHeight(); i++) {
            for (int j = 0; j < bigMatrix.getWidth(); j++) {
                bigMatrix.setValueAt(2, i, j); 
            }
        }
        long endTime = System.nanoTime();
        System.out.println("Took " + ((endTime - startTime) / 1000000) + " ms to fill bigMatrix.");

        startTime = System.nanoTime();
        Matrix bigAdd = bigMatrix.add(bigMatrix);
        endTime = System.nanoTime();
        System.out.println("Took " + ((endTime - startTime) / 1000000) + " ms to add bigAdd.");

        // startTime = System.nanoTime();
        // Matrix bigMul = bigMatrix.matrixMultiply(bigMatrix);
        // endTime = System.nanoTime();
        // System.out.println("Took " + ((endTime - startTime) / 1000000) + " ms to multiply bigMul.");

        // if (bigMatrix.getWidth() <= 80) {
        //     System.out.println("\n" + bigMul.toString());
        // } else {
        //     // check everything is correct
        //     boolean allCorrect = true;
        //     double expected = bigMul.getValueAt(0, 0);
        //     for (int i = 0; i < bigMul.getHeight(); i++) {
        //         for (int j = 0; j < bigMul.getWidth(); j++) {
        //             if (bigMul.getValueAt(i, j) != expected) {
        //                 allCorrect = false;
        //             }
        //         }
        //     }
        //     System.out.println("AllCorrect is " + allCorrect);
        // }

        System.out.println(testM3.transpose().toString() + "\n");
        System.out.println(testM2.transpose().toString() + "\n");

    }

}