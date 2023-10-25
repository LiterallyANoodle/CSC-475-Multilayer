package com.multilayer;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

// so I can call arbitrary functions
interface VirtualTableEntry {
    public void invoke();
}

public class Menu {

    public static final int MAX_THREADS = 3;

    public static final int TRAINING_DATA_SIZE = 60_000;
    public static final int TESTING_DATA_SIZE = 10_000;

    public static final String TRAINING_DATA_PATH = ".\\src\\main\\java\\com\\multilayer\\mnist_train.csv";
    public static final String TESTING_DATA_PATH = ".\\src\\main\\java\\com\\multilayer\\mnist_test.csv";

    public static final String DEFAULT_NETWORK_PATH = ".\\network.txt";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String UNICODE_FULL_FILL = "█";
    public static final String UNICODE_STRONG_FILL = "▓";
    public static final String UNICODE_MEDIUM_FILL = "▒";
    public static final String UNICODE_LIGHT_FILL = "░";
    public static final String UNICODE_NO_FILL = ".";

    private boolean networkLoaded = false;
    private String networkPath = DEFAULT_NETWORK_PATH;
    private String trainDataPath = TRAINING_DATA_PATH;
    private String testDataPath = TESTING_DATA_PATH;

    private Scanner scan = new Scanner(System.in);

    private NeuralNetwork mnist;
    
    public Menu() {
        this.networkLoaded = false;
        this.mnist = null;
    }

    public void start() {

        if (this.networkLoaded) {
            this.networkLoadedMenu();
        } else {
            this.networkUnloadedMenu();
        }

    }

    private void networkLoadedMenu() {
        String[] labels = new String[] {
            "Save and exit",
            "Train the loaded network",
            "Load an existing network from file",
            "Create a new network",
            "Try accuracy with training data",
            "Try accuracy with testing data",
            "View all classifications on testing set",
            "View incorrect classifications on testing set"
        };

        VirtualTableEntry[] vtable = new VirtualTableEntry[9];
        vtable[0] = () -> {
            this.scan.close(); 
            this.mnist.saveToFile(this.networkPath);
            System.exit(0);
        };
        vtable[1] = () -> {this.trainNetwork();};
        vtable[2] = () -> {this.loadNetwork();};
        vtable[3] = () -> {this.makeNewNetwork();};
        vtable[4] = () -> {this.trainingAccuracy();};
        vtable[5] = () -> {this.testingAccuracy();};
        vtable[6] = () -> {this.testingViewAllAscii();};
        vtable[7] = () -> {this.testingViewFalseAscii();};
        
        int response = 0;
        this.clearConsole();
        while (true) {

            this.printList(labels);
            System.out.print(">>> ");

            response = this.getInputInt();
            if (response < vtable.length && response >= 0) {
                break;
            }

            System.out.println(ANSI_RED + "That is not a valid item in the list!" + ANSI_WHITE + " Please try again: ");

        } 

        vtable[response].invoke();

    }

    private void networkUnloadedMenu() {
        
        String[] labels = new String[] {
            "Exit",
            "Create a new network",
            "Load an existing network from file"
        };

        VirtualTableEntry[] vtable = new VirtualTableEntry[3];
        vtable[0] = () -> { this.scan.close(); System.exit(0);};
        vtable[1] = () -> {this.makeNewNetwork();};
        vtable[2] = () -> {this.loadNetwork();};
        
        int response = 0;
        this.clearConsole();
        while (true) {

            this.printList(labels);
            System.out.print(">>> ");

            response = this.getInputInt();
            if (response < vtable.length && response >= 0) {
                break;
            }

            System.out.println(ANSI_RED + "That is not a valid item in the list!" + ANSI_WHITE + " Please try again: ");

        } 

        vtable[response].invoke();

    }

    private void makeNewNetwork() {
         
        String response = "";

        // System.out.println("Where should the network be saved? Do not use quotes. Leave blank for default path.");
        // System.out.print(">>> ");

        // response = this.getInputPath();

        if (response != "") {
            this.networkPath = response;
        }

        System.out.println("Making new network...");

        this.mnist = new NeuralNetwork(784, new int[] {15, 10}, 3);
        this.networkLoaded = true;
        this.networkLoadedMenu();
    }

    private void loadNetwork() {
        String response = "";

        // System.out.println("Where is the network saved? Do not use quotes. Leave blank for default path.");
        // System.out.print(">>> ");

        // response = this.getInputPath();

        if (response != "") {
            this.networkPath = response;
        }

        System.out.println("Loading network...");

        this.mnist = new NeuralNetwork(NeuralNetworkMemento.readFromFile(this.networkPath));
        this.networkLoaded = true;
        this.networkLoadedMenu();
    }

    private void printList(String[] labels) {

        for (int i = 0; i < labels.length; i++) {

            System.out.print("["+i+"] ");
            System.out.println(labels[i]);

        }

    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // bizarrely, closing one of these scanners also closes System.in??
    private int getInputInt() {
        try {
            // Scanner scan = new Scanner(System.in);

            int value = Integer.parseInt(this.scan.nextLine().strip());

            // scan.close();

            return value;

        } catch (Exception e) {
            System.out.println(ANSI_RED + "This is not a valid integer! " + ANSI_WHITE + " Please try again: ");
            System.out.print(">>> ");
            return getInputInt();
        }
    }

    private String getInputPath() {

        try {
            // Scanner scan = new Scanner(System.in);

            String path = this.scan.nextLine().strip();

            // scan.close();

            return path;
        } catch (Exception e) {
            System.out.println(ANSI_RED + "This is not a valid path! " + ANSI_WHITE + " Please try again: ");
            System.out.print(">>> ");
            return getInputPath();
        }
    }

    private void trainNetwork() {

        // System.out.println("Where is the training data saved? Do not use quotes. Leave blank for default path.");
        // System.out.print(">>> ");

        // this.trainDataPath = getInputPath();

        // System.out.println(this.trainDataPath);

        int epochs = 1;
        while (true) {

            System.out.println("How many epochs should be run?");
            System.out.print(">>> ");

            epochs = this.getInputInt();
            if (epochs >= 0) {
                break;
            }

            System.out.println(ANSI_RED + "That is not a valid epoch number!" + ANSI_WHITE + " Please try again: ");

        } 

        // off by one
        epochs = epochs++;

        // get training data
        DataPair[] trainingSet = null;
        try {
            System.out.println("Loading data...");
            trainingSet = DataSetHandler.readAllDataPairs(this.trainDataPath, TRAINING_DATA_SIZE);
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("Beginning training...");
        for (int epoch = 1; epoch < 2; epoch++) {
            this.mnist.stochasticGradientDescent(trainingSet, 2);
            System.out.println("Saving network state...");
            new NeuralNetworkMemento(mnist).saveToFile(this.networkPath);
        }

    }


    public void trainingAccuracy() {

        int[] correct = new int[] {0,0,0,0,0,0,0,0,0,0};
        int[] totals = new int[] {0,0,0,0,0,0,0,0,0,0};

        // get training data
        DataPair[] trainingSet = null;
        try {
            System.out.println("Loading data...");
            trainingSet = DataSetHandler.readAllDataPairs(this.trainDataPath, TRAINING_DATA_SIZE);
        } catch (Exception e) {
            System.err.println(e);
        }

        // run over the data once
        for (int i = 0; i < TRAINING_DATA_SIZE; i++) {

            Matrix output = this.mnist.forwardPass(trainingSet[i].getInputData());

            // find the most confident one: 
            double maxValue = 0;
            int maxLabel = 0;
            for (int j = 0; j < output.getHeight(); j++) {
                if (output.getValueAt(j, 0) > maxValue) {
                    maxLabel = j;
                }
            }

            if (maxLabel == trainingSet[i].getExpectedOutInt()) {
                correct[maxLabel]++;
            }

            totals[maxLabel]++;

        }

        // display with percentages
        System.out.println("\n" + ANSI_GREEN + "Total correct on training data:" + ANSI_WHITE + "\n");
        float percent = 0f;
        for (int i = 0; i < totals.length; i++) {
            percent = (float)correct[i] / (float)totals[i];
            System.out.println(ANSI_WHITE + i + ": " + correct[i] + "/" + totals[i] + ((percent >= 0.95) ? ANSI_GREEN : ANSI_CYAN) + percent + "%" + ANSI_WHITE);
        }

        System.out.println("Press enter to continue...");
        this.scan.nextLine();

        this.networkLoadedMenu();

    }

    public void testingAccuracy() {

        int[] correct = new int[] {0,0,0,0,0,0,0,0,0,0};
        int[] totals = new int[] {0,0,0,0,0,0,0,0,0,0};

        // get training data
        DataPair[] testingSet = null;
        try {
            System.out.println("Loading data...");
            testingSet = DataSetHandler.readAllDataPairs(this.testDataPath, TESTING_DATA_SIZE);
        } catch (Exception e) {
            System.err.println(e);
        }

        // run over the data once
        for (int i = 0; i < TESTING_DATA_SIZE; i++) {

            Matrix output = this.mnist.forwardPass(testingSet[i].getInputData());

            // find the most confident one: 
            double maxValue = 0;
            int maxLabel = 0;
            for (int j = 0; j < output.getHeight(); j++) {
                if (output.getValueAt(j, 0) > maxValue) {
                    maxLabel = j;
                }
            }

            if (maxLabel == testingSet[i].getExpectedOutInt()) {
                correct[maxLabel]++;
            }

            totals[maxLabel]++;

        }

        // display with percentages
        System.out.println("\n" + ANSI_GREEN + "Total correct on testing data:" + ANSI_WHITE + "\n");
        float percent = 0f;
        for (int i = 0; i < totals.length; i++) {
            percent = (float)correct[i] / (float)totals[i];
            System.out.println(ANSI_WHITE + i + ": " + correct[i] + "/" + totals[i] + ((percent >= 0.95) ? ANSI_GREEN : ANSI_CYAN) + percent + "%" + ANSI_WHITE);
        }

        System.out.println("Press enter to continue...");
        this.scan.nextLine();

        this.networkLoadedMenu();

    }

    public void testingViewAllAscii() {

        int[] correct = new int[] {0,0,0,0,0,0,0,0,0,0};
        int[] totals = new int[] {0,0,0,0,0,0,0,0,0,0};

        // get training data
        DataPair[] testingSet = null;
        try {
            System.out.println("Loading data...");
            testingSet = DataSetHandler.readAllDataPairs(this.testDataPath, TESTING_DATA_SIZE);
        } catch (Exception e) {
            System.err.println(e);
        }

        // run over the data once
        for (int i = 0; i < TESTING_DATA_SIZE; i++) {

            Matrix output = this.mnist.forwardPass(testingSet[i].getInputData());

            // find the most confident one: 
            double maxValue = 0;
            int maxLabel = 0;
            for (int j = 0; j < output.getHeight(); j++) {
                if (output.getValueAt(j, 0) > maxValue) {
                    maxLabel = j;
                }
            }

            if (maxLabel == testingSet[i].getExpectedOutInt()) {
                correct[maxLabel]++;
            }

            totals[maxLabel]++;

            printAscii(testingSet[i], maxLabel);

        }

        // display with percentages
        System.out.println("\n" + ANSI_GREEN + "Total correct on testing data:" + ANSI_WHITE + "\n");
        float percent = 0f;
        for (int i = 0; i < totals.length; i++) {
            percent = (float)correct[i] / (float)totals[i];
            System.out.println(ANSI_WHITE + i + ": " + correct[i] + "/" + totals[i] + ((percent >= 0.95) ? ANSI_GREEN : ANSI_CYAN) + percent + "%" + ANSI_WHITE);
        }

        System.out.println("Press enter to continue...");
        this.scan.nextLine();

        this.networkLoadedMenu();

    }

    public void testingViewFalseAscii() {

        int[] correct = new int[] {0,0,0,0,0,0,0,0,0,0};
        int[] totals = new int[] {0,0,0,0,0,0,0,0,0,0};

        // get training data
        DataPair[] testingSet = null;
        try {
            System.out.println("Loading data...");
            testingSet = DataSetHandler.readAllDataPairs(this.testDataPath, TESTING_DATA_SIZE);
        } catch (Exception e) {
            System.err.println(e);
        }

        // run over the data once
        for (int i = 0; i < TESTING_DATA_SIZE; i++) {

            Matrix output = this.mnist.forwardPass(testingSet[i].getInputData());

            // find the most confident one: 
            double maxValue = 0;
            int maxLabel = 0;
            for (int j = 0; j < output.getHeight(); j++) {
                if (output.getValueAt(j, 0) > maxValue) {
                    maxLabel = j;
                }
            }

            if (maxLabel == testingSet[i].getExpectedOutInt()) {
                correct[maxLabel]++;
            }

            totals[maxLabel]++;

            if (maxLabel != testingSet[i].getExpectedOutInt()) {
                printAscii(testingSet[i], maxLabel);
            }

        }

        // display with percentages
        System.out.println("\n" + ANSI_GREEN + "Total correct on testing data:" + ANSI_WHITE + "\n");
        float percent = 0f;
        for (int i = 0; i < totals.length; i++) {
            percent = (float)correct[i] / (float)totals[i];
            System.out.println(ANSI_WHITE + i + ": " + correct[i] + "/" + totals[i] + ((percent >= 0.95) ? ANSI_GREEN : ANSI_CYAN) + percent + "%" + ANSI_WHITE);
        }

        System.out.println("Press enter to continue...");
        this.scan.nextLine();

        this.networkLoadedMenu();

    }

    private void printAscii(DataPair dataPair, int guess) {

        boolean correct = (dataPair.getExpectedOutInt() == guess);

        this.clearConsole();
        System.out.println(ANSI_WHITE + "This number is labeled " + ANSI_YELLOW + dataPair.getExpectedOutInt() + ANSI_WHITE + " and the guess was " + ANSI_YELLOW + guess + ANSI_WHITE + " which is " + (correct ? ANSI_GREEN + "CORRECT!" : ANSI_RED + "FALSE!") + ANSI_WHITE );
        System.out.println();

        double value;
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                value = dataPair.getInputData().getValueAt(i*28+j, 0);
                if (value < 1f/5f) {
                    System.out.print(UNICODE_NO_FILL);
                } else if (value < 2f/5f) {
                    System.out.print(UNICODE_LIGHT_FILL);
                } else if (value < 3f/5f) {
                    System.out.print(UNICODE_MEDIUM_FILL);
                } else if (value < 4f/5f) {
                    System.out.print(UNICODE_STRONG_FILL);
                } else {
                    System.out.print(UNICODE_FULL_FILL);
                }
            }
            System.out.println();
        }

        System.out.println("\n" + ANSI_WHITE + "Press enter to continue or any other key to exit...");
        if (this.scan.nextLine().strip() != "") {
            this.networkLoadedMenu();
        } 


    }

}
