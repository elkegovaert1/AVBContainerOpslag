import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Yard yard = new Yard("data/L40_W24_H3_Q1_C3(1).ysi");
        yard.checkContainers();
        yard.writeCraneMovements("data/output.ysi");

    }
}


