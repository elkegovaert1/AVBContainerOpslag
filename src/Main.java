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
            Yard yard = Reader.read(args[0]);
            yard.moveCrane();
            Writer.write(args[1], yard);
        }

        Yard yard = Reader.read("data/L40_W24_H3_Q1_C3.ysi");
        yard.moveCrane();
        Writer.write("data/L40_W24_H3_Q1_C3.yso", yard);

        Yard yard1 = Reader.read("data/L100_W48_H3_Q1_C4.ysi");
        yard1.moveCrane();
        Writer.write("data/L100_W48_H3_Q1_C4.yso", yard1);

        Yard yard2 = Reader.read("data/L1000_W240_H3_Q1_C1000.ysi");
        yard2.moveCrane();
        Writer.write("data/L1000_W240_H3_Q1_C1000.yso", yard2);

        Yard yard3 = Reader.read("data/example.ysi");
        yard3.moveCrane();
        Writer.write("data/example.yso", yard3);

    }
}


