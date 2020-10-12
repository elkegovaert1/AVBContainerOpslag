import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int L;
        int W;
        int Hmax;
        int Ls;
        int Ws;


        try {
            File myObj = new File("L40_W24_H3_Q1_C3(1).ysi");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                switch (data) {
                    case "# L,W,H_max":
                        data = myReader.nextLine();
                        String[] split = data.split(",");
                        L = Integer.parseInt(split[0]);
                        W = Integer.parseInt(split[1]);
                        Hmax = Integer.parseInt(split[2]);
                        data = myReader.nextLine();
                        break;
                    case "# L_s,W_s":
                        data = myReader.nextLine();
                        String[] split2 = data.split(",");
                        Ls = Integer.parseInt(split2[0]);
                        Ws = Integer.parseInt(split2[1]);
                        data = myReader.nextLine();
                        break;
                    case "# slots":

                }

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
