package com.multilayer;

import java.util.Arrays;

class Matrix {

    private static short DEBUG = 0;

    // the first bracket shall refer to a row 
    // the second bracket shall refer to a column
    // m is the height, n is the width for an m x n matrix
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
    // I don't want users to have access to a matrix with an array of null values
    public Matrix(int height, int width) {
        float[][] values = fillAllZeros(new float[height][width]);
        this.setValues(values);
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

    // accessors and mutators 
    public float[][] getValues() {
        return this.values;
    }

    public float getValueAt(int i, int j) {
        return this.values[i][j];
    }

    public Matrix getRow(int i) {
        return new Matrix(new float[][] {this.values[i]});
    }

    public Matrix getColumn(int j) {
        Matrix output = new Matrix(this.getHeight(), 1);
        for (int i = 0; i < this.getHeight(); i++) {
            output.setValueAt(this.getValueAt(i, j), i, 0);
        }
        return output;
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

    public boolean isSameDimensionsAs(Matrix other) {
        if ((this.getHeight() == other.getHeight()) && (this.getWidth() == other.getWidth())) {
            return true; 
        }
        return false;
    }

    public boolean isRowVector() {
        if (this.getHeight() == 1) {
            return true;
        }
        return false;
    }

    public boolean isColumnVector() {
        if (this.getWidth() == 1) {
            return true;
        }
        return false;
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

    public Matrix add(Matrix other) {

        try {

            if (this.isSameDimensionsAs(other)) {

                Matrix sum = new Matrix(this.getHeight(), this.getWidth());

                for (int i = 0; i < this.getHeight(); i++) {
                    for (int j = 0; j < this.getWidth(); j++) {
                        sum.setValueAt(this.getValueAt(i, j) + other.getValueAt(i, j), i, j);
                    }
                }

                return sum;
            } else {
                throw new MatrixException("Added matrices are not the same dimensions.");
            }
            
        } catch (MatrixException e) {
            System.out.println(e.toString());
        }

        return null;

    }

    public float dotProduct(Matrix other) throws MatrixException {

        if (DEBUG >= 3) {
            System.out.println("Dotting left matrix: \n" + this.toString());
            System.out.println("Against right matrix: \n" + other.toString());
        }

        // dot products technically can only be applied to a row "this" vector and a column "other" vector
        // the width of "this" must be the same as the height of "other"

        if (this.isRowVector() && other.isColumnVector()) {

            if (this.getWidth() == other.getHeight()) {

                float product = 0f;

                for (int i = 0; i < other.getHeight(); i++) {
                    product += this.getValueAt(0, i) * other.getValueAt(i, 0);
                }

                if (DEBUG >= 2) {
                    System.out.println("Dotted product is: " + product + "\n");
                }

                return product;

            } else {
                throw new MatrixException("Dot product can only be performed on a row vector and column vector. The row vector width must be equal to the column vector height.");
            }

        } else {
            throw new MatrixException("Dot product can only be performed on a row vector against a column vector. Did you mean to multiply them the other way around?");
        }

    }

    public Matrix matrixMultiply(Matrix other) {

        // the width of the left matrix must be the same as the height of the right matrix
        // the resulting matrix will have the width of the right matrix and the height of the left matrix
        
        try {

            if (this.getWidth() == other.getHeight()) {

                Matrix product = new Matrix(this.getHeight(), other.getWidth());

                for (int i = 0; i < product.getHeight(); i++) {
                    for (int j = 0; j < product.getWidth(); j++) {
                        product.setValueAt(this.getRow(i).dotProduct(other.getColumn(j)), i, j);
                    }
                }

                return product;

            } else {
                throw new MatrixException("Matrix multiplication can only be performed if the left matrix width is the same as the right matrix height.");
            }

        } catch (MatrixException e) {
            System.out.println(e.toString());
        }

        return null;

    }

}