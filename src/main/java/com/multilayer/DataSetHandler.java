package com.multilayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataSetHandler {
    
    // we know the size of the data that will be inputted 
    // label + 28x28 = 785 total numbers (shorts) which will be converted to doubles when normalized

    // everything can be preloaded as column vectors and their references can be passed around

    public static void readDataPair(String filePath) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(filePath));

        scanner.useDelimiter(",");

        System.out.println(scanner.nextLine());

    }

}
