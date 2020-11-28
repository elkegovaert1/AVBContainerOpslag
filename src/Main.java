import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //LABO 2

        // argumenten voor JAR
        if(args.length == 2){
            Yard yard = Reader.read(args[0]);
            yard.checkYard();
            Writer.write(args[1], yard);
        }

        Yard yard = Reader.read("data/example2.ysi");
        yard.checkContainers(); // visuele controle van yard
        yard.checkYard();
        yard.checkContainers(); // visuele controle van yard
        Writer.write("data/example2.yso", yard);

        /* LABO 2 voorbeelden + jar
        // argumenten voor JAR
        if(args.length == 2){
            Yard yard = Reader.read(args[0]);
            yard.checkYard();
            Writer.write(args[1], yard);
        }

        Yard yard = Reader.read("data/L500_W60_H3_Q1_C10.ysi");
        yard.checkContainers(); // visuele controle van yard
        yard.checkYard();
        yard.checkContainers(); // visuele controle van yard
        Writer.write("data/L500_W60_H3_Q1_C10.yso", yard);
        */


        /* LABO 1 voorbeelden + JAR

        // argumenten voor JAR
        if(args.length == 2){
            Yard yard = Reader.read(args[0]);
            yard.moveCrane();
            Writer.write(args[1], yard);
        }

        Yard yard = Reader.read("data/L40_W24_H3_Q1_C3.ysi");
        Writer.write("data/L40_W24_H3_Q1_C3.yso", yard);

        Yard yard1 = Reader.read("data/L100_W48_H3_Q1_C4.ysi");
        Writer.write("data/L100_W48_H3_Q1_C4.yso", yard1);

        Yard yard2 = Reader.read("data/L1000_W240_H3_Q1_C1000.ysi");
        Writer.write("data/L1000_W240_H3_Q1_C1000.yso", yard2);

        Yard yard3 = Reader.read("data/example.ysi");
        Writer.write("data/example.yso", yard3);
        */

    }
}


