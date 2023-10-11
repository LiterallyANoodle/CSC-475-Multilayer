package com.multilayer;

import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;

class Main {

    public static void main(String[] args) {

        // basic test matrices
        Matrix testM1 = new Matrix(3, 3);
        Matrix testM2 = new Matrix(new float[][] {{1f, 2f, 3f},
                                                  {4f, 5f, 6f},
                                                  {7f, 8f, 9f}});
        Matrix testM3 = new Matrix(new float[][] {{1f, 2f},
                                                  {3f, 4f}});

        // Standardized matrices from assignment pdf: 
        Matrix L1TestWeights = new Matrix(new float[][] {{-0.21f, 0.72f, -0.25f, 1f}, 
                                                         {-0.94f, -0.41f, -0.47f, 0.63f},
                                                         {0.15f, 0.55f, -0.49f, -0.75f}});
        Matrix L1TestBiases = new Matrix(new float[][] {{0.1f},
                                                        {-0.36f},
                                                        {-0.31f}});
        Matrix L2TestWeights = new Matrix(new float[][] {{0.76f, 0.48f, -0.73f},
                                                         {0.34f, 0.89f, -0.23f}});
        Matrix L2TestBiases = new Matrix(new float[][] {{0.16f},
                                                        {-0.46f}});

        float testLearningRate = 10f;

        // print assignment matrices 
        System.out.println("Assignment matrices ----------");
        System.out.println(L1TestWeights.toString() + "\n");
        System.out.println(L1TestBiases.toString() + "\n");
        System.out.println(L2TestWeights.toString() + "\n");
        System.out.println(L2TestBiases.toString() + "\n");

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

        startTime = System.nanoTime();
        Matrix bigMul = bigMatrix.matrixMultiply(bigMatrix);
        endTime = System.nanoTime();
        System.out.println("Took " + ((endTime - startTime) / 1000000) + " ms to multiply bigMul.");

        if (bigMatrix.getWidth() <= 80) {
            System.out.println("\n" + bigMul.toString());
        } else {
            // check everything is correct
            boolean allCorrect = true;
            float expected = bigMul.getValueAt(0, 0);
            for (int i = 0; i < bigMul.getHeight(); i++) {
                for (int j = 0; j < bigMul.getWidth(); j++) {
                    if (bigMul.getValueAt(i, j) != expected) {
                        allCorrect = false;
                    }
                }
            }
            System.out.println("AllCorrect is " + allCorrect);
        }

    }

}