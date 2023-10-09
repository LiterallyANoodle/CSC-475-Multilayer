package com.multilayer;

import java.util.Arrays;

class Matrix {

    // the first bracket shall refer to a row 
    // the second bracket shall refer to a column
    // m is the height, n is the width for an n x m matrix
    // an item in the matrix is referred to by a_i,j where 
    // i traverses the matrix vertically downward and 
    // j traverses the matrix horizontally rightward 
    // therefore, indexing is referred to by matrix[i][j] in code
    private float[][] values; 

    // init a matrix with nothing
    public Matrix() {
        this.values = null;
    }
    
    // init a matrix m x n with selected values
    public Matrix(float[][] values) {
        this.setValues(values);
    }

    // init a matrix m x n with all zeros
    public Matrix(int width, int height) {
        float[][] values = fillAllZeros(new float[height][width]);
        this.setValues(values);
    }

    // accessors and mutators 
    public float[][] getValues() {
        return this.values;
    }

    public float getValueAt(int i, int j) {
        return this.values[i][j];
    }
    
    public void setValues(float[][] values) {
        // ensure the rows are all the same length 
        // a jagged 2D array is not a matrix! 
        int standardLength = values[0].length;

        try {
            for (int i = 1; i < values.length; i++) {
                if (values[i].length != standardLength) {
                    throw new MatrixException("Unable to set matrix values. Rows are not all the same length.");
                }
            }
        } catch (MatrixException e) {
            System.out.println(e.toString());
            return;
        }

        this.values = values;
    }

    public void setValueAt(float value, int i, int j) {
        this.values[i][j] = value;
    }

    // utilities 
    public int getWidth() {
        return this.values[0].length;
    }

    public int getHeight() {
        return this.values.length;
    }

    public String toString() {
        String output = "";
        for (int i = 0; i < this.getHeight(); i++) {
            output += Arrays.toString(this.values[i]);
            if (i != this.getHeight() - 1) {
                output += "\n";
            }
        }
        return output;
    }

    // this is just to initialize a "blank" array for a new matrix
    // not to be used elsewhere
    private static float[][] fillAllZeros(float[][] valuesArray) {
        for (int i = 0; i < valuesArray.length; i++) {
            for (int j = 0; j < valuesArray[0].length; j++) {
                valuesArray[i][j] = 0;
            }
        }
        return valuesArray;
    }

}