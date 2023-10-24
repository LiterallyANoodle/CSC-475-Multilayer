package com.multilayer;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

// so I can call arbitrary functions
interface VirtualTableEntry {
    public void invoke();
}

public class Menu {

    public static int MAX_THREADS = 3;

    public static int TRAINING_DATA_SIZE = 60_000;
    public static int TESTING_DATA_SIZE = 10_000;

    public static String TRAINING_DATA_PATH = ".\\src\\main\\java\\com\\multilayer\\mnist_train.csv";
    public static String TESTING_DATA_PATH = ".\\src\\main\\java\\com\\multilayer\\mnist_test.csv";

    public static String DEFAULT_NETWORK_PATH = ".\\network.txt";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    private boolean networkLoaded = false;
    private String networkPath = DEFAULT_NETWORK_PATH;
    private String trainDataPath = TRAINING_DATA_PATH;
    private String testDataPath = TESTING_DATA_PATH;

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
            "Exit",
            "Train the loaded network",
            "Load an existing network from file",
            "Create a new network"
        };

        VirtualTableEntry[] vtable = new VirtualTableEntry[9];
        vtable[0] = () -> {System.exit(0);};
        vtable[1] = () -> {this.trainNetwork();};
        vtable[2] = () -> {this.loadNetwork();};
        vtable[3] = () -> {this.makeNewNetwork();};
        
        int response = 0;
        this.clearConsole();
        do {

            this.printList(labels);
            System.out.print(">>>");

            response = this.getInputInt();
            if (response < vtable.length && response >= 0) {
                break;
            }

            System.out.println(ANSI_RED + "That is not a valid item in the list!" + ANSI_WHITE + " Please try again: ");

        } while (true);

        vtable[response].invoke();

    }

    private void networkUnloadedMenu() {
        
        String[] labels = new String[] {
            "Exit",
            "Create a new network",
            "Load an existing network from file"
        };

        VirtualTableEntry[] vtable = new VirtualTableEntry[3];
        vtable[0] = () -> {System.exit(0);};
        vtable[1] = () -> {this.makeNewNetwork();};
        vtable[2] = () -> {this.loadNetwork();};
        
        int response = 0;
        this.clearConsole();
        do {

            this.printList(labels);
            System.out.print(">>>");

            response = this.getInputInt();
            if (response < vtable.length && response >= 0) {
                break;
            }

            System.out.println(ANSI_RED + "That is not a valid item in the list!" + ANSI_WHITE + " Please try again: ");

        } while (true);

        vtable[response].invoke();

    }

    private void makeNewNetwork() {
         
        String response = "";

        System.out.println("Where should the network be saved? Do not use quotes. Leave blank for default path.");
        System.out.print(">>>");

        response = this.getInputPath();

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

        System.out.println("Where is the network saved? Do not use quotes. Leave blank for default path.");
        System.out.print(">>>");

        response = this.getInputPath();

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

    private int getInputInt() {
        try {
            Scanner scan = new Scanner(System.in);

            scan.close();

            return Integer.parseInt(scan.nextLine().strip());

        } catch (Exception e) {
            System.out.println(ANSI_RED + "This is not a valid integer! " + ANSI_WHITE + " Please try again: ");
            System.out.print(">>>");
            return getInputInt();
        }
    }

    private String getInputPath() {
        try {
            Scanner scan = new Scanner(System.in);

            String path = scan.nextLine().strip();

            scan.close();

            return path;
        } catch (Exception e) {
            System.out.println(ANSI_RED + "This is not a valid path! " + ANSI_WHITE + " Please try again: ");
            System.out.print(">>>");
            return getInputPath();
        }
    }

    private void trainNetwork() {

        System.out.println("Where is the training data saved? Do not use quotes. Leave blank for default path.");
        System.out.print(">>>");
        
        this.trainDataPath = getInputPath();

        int epochs = 1;
        do {

            System.out.println("How many epochs should be run?");
            System.out.print(">>>");

            epochs = this.getInputInt();
            if (epochs >= 0) {
                break;
            }

            System.out.println(ANSI_RED + "That is not a valid epoch number!" + ANSI_WHITE + " Please try again: ");

        } while (true);

        // off by one
        epochs = epochs++;

        // get training data
        DataPair[] trainingSet = null;
        try {
            trainingSet = DataSetHandler.readAllDataPairs(this.trainDataPath, 60_000);
        } catch (Exception e) {
            System.out.println(e);
        }

        for (int epoch = 1; epoch < 2; epoch++) {
            this.mnist.setEpoch(epoch);
            this.mnist.stochasticGradientDescent(trainingSet, 2);
        }

    }

}
