import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // argumenten voor JAR
        if(args.length == 2){
            Yard yard = new Yard(args[0]);
            yard.writeCraneMovements(args[1]);
        }



        Yard yard = new Yard("data/L40_W24_H3_Q1_C3.ysi");
        yard.writeCraneMovements("data/L40_W24_H3_Q1_C3.yso");

        yard.findSlots();

        Yard yard1 = new Yard("data/L100_W48_H3_Q1_C4.ysi");
        yard1.writeCraneMovements("data/L100_W48_H3_Q1_C4.yso");

        Yard yard2 = new Yard("data/L1000_W240_H3_Q1_C1000.ysi");
        yard2.writeCraneMovements("data/L1000_W240_H3_Q1_C1000.yso");

        Yard yard3 = new Yard("data/example.ysi");
        yard3.writeCraneMovements("data/example.yso");



    }
}


