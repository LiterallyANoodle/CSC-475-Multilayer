package com.multilayer;

class Main {

    public static void main(String[] args) {

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

        System.out.println(L1TestWeights.toString() + "\n");
        System.out.println(L1TestBiases.toString() + "\n");
        System.out.println(L2TestWeights.toString() + "\n");
        System.out.println(L2TestBiases.toString() + "\n");

    }

}