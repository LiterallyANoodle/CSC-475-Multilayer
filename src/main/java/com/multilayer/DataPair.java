package com.multilayer;

// Just for the sake of organization because Matrix[][] is really nasty
public class DataPair {
    
    private Matrix inputData;
    private Matrix expectedOutput;

    public DataPair(Matrix inputData, Matrix expectedOutput) {
        this.inputData = inputData;
        this.expectedOutput = expectedOutput;
    }

    public Matrix getInputData() {
        return this.inputData;
    }

    public Matrix getExpectedOutput() {
        return this.expectedOutput;
    }

    // no need for mutators

    public String toString() {
        return "Input:\n" + this.getInputData() + "\nExpected output:\n" + this.getExpectedOutput();
    }

}
