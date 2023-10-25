package com.multilayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataSetHandler {

    public static int DEBUG = 2;
    
    // we know the size of the data that will be inputted 
    // label + (28x28 = 784) = 785 total numbers (shorts) which will be converted to doubles when normalized

    // everything can be preloaded as column vectors and their references can be passed around

    public static DataPair[] readAllDataPairs(String filePath, int setSize) throws FileNotFoundException {

        DataPair[] allPairs = new DataPair[setSize];

        Scanner scanner = new Scanner(new File(filePath));

        for (int i = 0; i < setSize; i++) {

            allPairs[i] = readAllDataPairsHelper(scanner.nextLine());

        }

        return allPairs;

    }

    // processes a single line from the csv 
    public static DataPair readAllDataPairsHelper(String input) {

        String[] inputStrList = input.split(",");

        // make the label "output" matrix
        int labelInt = Integer.parseInt(inputStrList[0]);
        Matrix labelMat = new Matrix(10, 1); 
        labelMat.setValueAt(1, labelInt, 0);

        // fill row Matrix of normalized pixel data 
        double[] imgDataValues = new double[784];
        for (int i = 0; i < 784; i++) {
            imgDataValues[i] = normalize255(Integer.parseInt(inputStrList[i+1]));
        }
        // then init matrix... -> transpose to column...
        Matrix imgDataMat = new Matrix(new double[][] {imgDataValues}).transpose();

        return new DataPair(imgDataMat, labelMat, labelInt);

    }

    public static double normalize255(int input) {
        return (input / 255d);
    }

}
