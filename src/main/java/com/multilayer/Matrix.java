package com.multilayer;

// import java.util.Arrays;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Matrix {

    private static short DEBUG = 2;
    private static int MAX_THREADS = 5;

    // the first bracket shall refer to a row 
    // the second bracket shall refer to a column
    // m is the height, n is the width for an m x n matrix
    // an item in the matrix is referred to by a_i,j where 
    // i traverses the matrix vertically downward and 
    // j traverses the matrix horizontally rightward 
    // therefore, indexing is referred to by matrix[i][j] in code
    private double[][] values; 

    // init a matrix with nothing
    public Matrix() {
        this.values = null;
    }
    
    // init a matrix m x n with selected values
    public Matrix(double[][] values) {
        this.setValues(values);
    }

    // init a matrix m x n with all zeros
    // I don't want users to have access to a matrix with an array of null values
    public Matrix(int height, int width) {
        double[][] values = fillAllZeros(new double[height][width]);
        this.setValues(values);
    }

    // copy constructor 
    public Matrix(Matrix other) {
        double[][] values = new double[other.getHeight()][other.getWidth()];
        for (int i = 0; i < other.getHeight(); i ++) {
            for (int j = 0; j < other.getWidth(); j++) {
                values[i][j] = other.getValueAt(i, j);
            }
        }
        this.values = values;
    }

    // this is just to initialize a "blank" array for a new matrix
    // not to be used elsewhere
    private static double[][] fillAllZeros(double[][] valuesArray) {
        for (int i = 0; i < valuesArray.length; i++) {
            for (int j = 0; j < valuesArray[0].length; j++) {
                valuesArray[i][j] = 0;
            }
        }
        return valuesArray;
    }

    // accessors and mutators 
    public double[][] getValues() {
        return this.values;
    }

    public double getValueAt(int i, int j) {
        return this.values[i][j];
    }

    public Matrix getRow(int i) {
        return new Matrix(new double[][] {this.values[i]});
    }

    public Matrix getColumn(int j) {
        Matrix output = new Matrix(this.getHeight(), 1);
        for (int i = 0; i < this.getHeight(); i++) {
            output.setValueAt(this.getValueAt(i, j), i, 0);
        }
        return output;
    }
    
    public void setValues(double[][] values) {
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

    public void setValueAt(double value, int i, int j) {
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
            // output += Arrays.toString(this.values[i]); // He said in class not to use Arrays, so...... gotta make the inner string by hand
            output += "[";
            for (int j = 0; j < this.getWidth(); j++) {
                output += this.getValueAt(i, j);
                if (j != this.getWidth() - 1) {
                    output += ", ";
                }
            }
            output += "]\n";
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

    public Matrix subtract(Matrix other) {

        try {

            if (this.isSameDimensionsAs(other)) {

                Matrix sum = new Matrix(this.getHeight(), this.getWidth());

                for (int i = 0; i < this.getHeight(); i++) {
                    for (int j = 0; j < this.getWidth(); j++) {
                        sum.setValueAt(this.getValueAt(i, j) - other.getValueAt(i, j), i, j);
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

    public double dotProduct(Matrix other) throws MatrixException {

        if (DEBUG >= 5) {
            System.out.println("Dotting left matrix: \n" + this.toString());
            System.out.println("Against right matrix: \n" + other.toString());
        }

        // dot products technically can only be applied to a row left vector and a column right vector
        // the width of left must be the same as the height of right

        if (this.isRowVector() && other.isColumnVector()) {

            if (this.getWidth() == other.getHeight()) {

                double product = 0f;

                for (int i = 0; i < other.getHeight(); i++) {
                    product += this.getValueAt(0, i) * other.getValueAt(i, 0);
                }

                if (DEBUG >= 4) {
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

    public Matrix transpose() {
        Matrix output = new Matrix(this.getWidth(), this.getHeight());

        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                output.setValueAt(this.getValueAt(i, j), j, i);
            }
        }

        return output;
    }

    // subclass to assist in multiplication
    private class MatrixMultiplyTask implements Runnable {

        private int i;
        private int j;

        private Matrix left;
        private Matrix right;
        private Matrix product;

        public MatrixMultiplyTask(int i, int j, Matrix left, Matrix right, Matrix product) {
            super();
            this.i = i;
            this.j = j;
            this.left = left;
            this.right = right;
            this.product = product;
        }

        @Override
        public void run() {

            try {

                // dot product to set the value at i, j
                double value = this.left.getRow(this.i).dotProduct(this.right.getColumn(this.j));
                product.setValueAt(value, this.i, this.j);

                if (DEBUG >= 3) {
                    System.out.println("Set value to " + value + " at " + this.i + ", " + this.j);
                }

            } catch (MatrixException e) {
                System.out.println(e.toString());
            }
            
        }

    }

    public Matrix matrixMultiply(Matrix other) {

        // the width of the left matrix must be the same as the height of the right matrix
        // the resulting matrix will have the width of the right matrix and the height of the left matrix
        
        try {

            if (this.getWidth() == other.getHeight()) {

                Matrix product = new Matrix(this.getHeight(), other.getWidth());

                // create task array
                Runnable[][] tasks = new Runnable[product.getHeight()][product.getWidth()];

                // create thread pool
                ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

                // fill and execute tasks
                for (int i = 0; i < product.getHeight(); i++) {
                    for (int j = 0; j < product.getWidth(); j++) {
                        tasks[i][j] = new MatrixMultiplyTask(i, j, this, other, product);
                        pool.execute(tasks[i][j]);
                    }
                }

                // destroy the pool (stop accepting new tasks)
                pool.shutdown();

                // await all tasks to finish
                pool.awaitTermination(120, TimeUnit.SECONDS); // Timeout in 2 minutes. Significantly greater than expected max time.

                return product;

            } else {
                throw new MatrixException("Matrix multiplication can only be performed if the left matrix width is the same as the right matrix height.");
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return null;

    }

    public Matrix scalarMultiply(double scalar) {

        Matrix output = new Matrix(this);

        for (int i = 0; i < output.getHeight(); i++) {
            for (int j = 0; j < output.getWidth(); j++) {
                output.setValueAt(scalar * output.getValueAt(i, j), i, j);
            }
        }

        return output;

    }

}