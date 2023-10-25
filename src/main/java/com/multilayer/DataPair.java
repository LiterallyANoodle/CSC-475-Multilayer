package com.multilayer;

// Just for the sake of organization because Matrix[][] is really nasty
public class DataPair {
    
    private Matrix inputData;
    private Matrix expectedOutput;
    private int expectedOutInt; // useful to just have the label as an int

    public DataPair(Matrix inputData, Matrix expectedOutput, int expectedOutInt) {
        this.inputData = inputData;
        this.expectedOutput = expectedOutput;
        this.expectedOutInt = expectedOutInt;
    }

    public Matrix getInputData() {
        return this.inputData;
    }

    public Matrix getExpectedOutput() {
        return this.expectedOutput;
    }

    public int getExpectedOutInt() {
        return this.expectedOutInt;
    }

    // no need for mutators

    public String toString() {
        return "Input:\n" + this.getInputData() + "\nExpected output:\n" + this.getExpectedOutInt();
    }

}
