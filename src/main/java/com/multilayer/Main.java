package com.multilayer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Random;

class Main {

    public static int MAX_THREADS = 3;

    public static void main(String[] args) {

        // create network of defined shape
        NeuralNetwork mnist = new NeuralNetwork(784, new int[] {15, 10}, 3);

        long time = System.nanoTime();
        trainNetwork(mnist, 5);
        time = System.nanoTime() - time;
        System.out.println("Took: " + time / 1000000 + "ms");
        
    }

    private static void trainNetwork(NeuralNetwork network, int epochs) {

        // off by one
        epochs = epochs++;

        // get training data
        DataPair[] trainingSet = null;
        try {
            trainingSet = DataSetHandler.readAllDataPairs(".\\src\\main\\java\\com\\multilayer\\mnist_train.csv", 60_000);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }

        // make random sequence of indexes 
        int[] randomIndexes = randomPermutation(60_000);

        for (int epoch = 1; epoch < 2; epoch++) {
            network.setEpoch(epoch);
            network.stochasticGradientDescent(trainingSet, randomIndexes, 2, epoch);
        }

        for (int i = 0; i < 20; i++) {
            System.out.println("Output of Forward pass with Training data " + (i+1) + " with final network: \n" + network.forwardPass(trainingSet[randomIndexes[i]].getInputData()) + "\n" + "Expected:\n" + trainingSet[randomIndexes[i]].getExpectedOutput());
        }

    }

    // naiive method of making a random permutation
    private static int[] randomPermutation(int size) {

        Random rand = new Random(System.nanoTime());

        int[] randArr = new int[size];
        boolean[] tracker = new boolean[size];

        for (int i = 0; i < size; i++) {

            while (true) {

                int attempt = rand.nextInt(size);

                if (tracker[attempt] == false) {
                    randArr[i] = attempt;
                    tracker[attempt] = true;
                    break;
                }

            }

        }

        return randArr;

    }

    // this randomizer algorithm was inspired by Heap's algorithm: https://en.wikipedia.org/wiki/Heap%27s_algorithm
    // the key difference is that there's no swapping taking place 
    // also a random recursive walk is taken down the tree that hits every leaf (integer) in a random order and appends them to the list
    // this definitely has some bias but also no collisions -- its good enough for us

    // so this doesn't work, im just going to use the naiive method 
    private static ArrayList<Integer> randomPermutationWalk(int size) {

        ArrayList<Integer> randomIndexes = new ArrayList<Integer>();
        Random rand = new Random(System.nanoTime());

        randomPermutationWalkHelper(randomIndexes, rand, 0, size);

        return randomIndexes;

    }

    private static void randomPermutationWalkHelper(ArrayList<Integer> list, Random rand, int start, int end) {

        // System.out.println("Start = " + start + " , End = " + end);

        int walk = rand.nextInt(2);

        if (end - start == 1) {
            if (walk == 1) {
                list.add(start);
                list.add(end);
            } else {
                list.add(end);
                list.add(start);
            }
            // System.out.println("Returning...");
            return;
        }

        if (walk == 1) {
            // System.out.println("Walk 1");
            randomPermutationWalkHelper(list, rand, start, ((end - start) / 2) + start);
            randomPermutationWalkHelper(list, rand, ((end - start) / 2) + start, end);
        } else {
            // System.out.println("Walk 0");
            randomPermutationWalkHelper(list, rand, ((end - start) / 2) + start, end);
            randomPermutationWalkHelper(list, rand, start, ((end - start) / 2) + start);
        }

    }

    // silly little experiment about randomizing the sequences using race conditions :) 
    private void fafo() {

        // abuse race conditions to generate the random number sequence (very silly teehee): 
        ArrayList<Integer> randIndexes = new ArrayList<Integer>();
        ExecutorService superPool = Executors.newFixedThreadPool(MAX_THREADS);
        ExecutorService subPool = Executors.newFixedThreadPool(MAX_THREADS);
        RandomizeTask[] tasks = new RandomizeTask[500];
        for (int i = 0; i < 500; i++) {
            tasks[i] = new RandomizeTask(i, randIndexes);
        }
        superPool.execute(new RandomizeSuperTask(subPool, tasks, 0, 100));
        superPool.execute(new RandomizeSuperTask(subPool, tasks, 100, 200));
        superPool.execute(new RandomizeSuperTask(subPool, tasks, 200, 300));
        superPool.execute(new RandomizeSuperTask(subPool, tasks, 300, 400));
        superPool.execute(new RandomizeSuperTask(subPool, tasks, 400, 500));
        try {
            superPool.shutdown();
            superPool.awaitTermination(120, TimeUnit.SECONDS);
            subPool.shutdown();
            subPool.awaitTermination(120, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println(randIndexes);

    }

    // silly little experiment about randomizing the sequences using race conditions :) 
    private class RandomizeSuperTask implements Runnable {

        private ExecutorService pool;
        private RandomizeTask[] tasks;
        private int rangeStart;
        private int rangeEnd;

        public RandomizeSuperTask(ExecutorService pool, RandomizeTask[] tasks, int rangeStart, int rangeEnd) {
            super();
            this.pool = pool;
            this.tasks = tasks;
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
        }

        @Override
        public void run() {
            for (int i = rangeStart; i < rangeEnd; i++) {
                pool.execute(tasks[i]);
            }
        }

    }

    // silly little experiment about randomizing the sequences using race conditions :) 
    private class RandomizeTask implements Runnable {

        private int i;
        private ArrayList<Integer> randList;

        public RandomizeTask(int i, ArrayList<Integer> randList) {
            super();
            this.i = i;
            this.randList = randList;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2);
            } catch (Exception e) {}
            this.randList.add(i);
        }

    }

    private static void neuralNetTesting() {

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

        NeuralNetwork testNet = new NeuralNetwork(4, new int[] {3, 2}, 10);

        testNet.getLayer(1).setWeights(L1TestWeights);
        testNet.getLayer(1).setBiases(L1TestBiases);
        testNet.getLayer(2).setWeights(L2TestWeights);
        testNet.getLayer(2).setBiases(L2TestBiases);

        // System.out.println(testNet.toString());

        Matrix testResult1 = testNet.forwardPass(testInput1);
        // System.out.println("Test Foward Pass Result: \n" + testResult1);

        // training set for toy network: 
        // array of 4 training pairs 
        // each pair contains the input vector and expected output vector 
        DataPair[] trainingData = new DataPair[4];

        // input pair 1
        trainingData[0] = new DataPair(new Matrix(new double[][] {{0}, {1}, {0}, {1}}), new Matrix(new double[][] {{0}, {1}}));

        // input pair 2
        trainingData[1] = new DataPair(new Matrix(new double[][] {{1}, {0}, {1}, {0}}), new Matrix(new double[][] {{1}, {0}}));

        // input pair 3
        trainingData[2] = new DataPair(new Matrix(new double[][] {{0}, {0}, {1}, {1}}), new Matrix(new double[][] {{0}, {1}}));
        
        // input pair 4
        trainingData[3] = new DataPair(new Matrix(new double[][] {{1}, {1}, {0}, {0}}), new Matrix(new double[][] {{1}, {0}}));

        // for (int epoch = 1; epoch < 7; epoch++) {
        //     testNet.stochasticGradientDescent(trainingData, 2, epoch);
        // }
        // for (int i = 0; i < trainingData.length; i++) {
        //     System.out.println("Output of Forward pass with Training data " + (i+1) + " with final network: \n" + testNet.forwardPass(trainingData[i].getInputData()) + "\n");
        // }

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