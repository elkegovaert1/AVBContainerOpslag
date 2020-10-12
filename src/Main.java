import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int L = 0;
        int W = 0;
        int Hmax = 0;
        int Ls = 0;
        int Ws = 0;
        int currentHeight = 0;
        Slot [][][] slots = null;
        List<Slot> slotlist = new ArrayList<>();
        List<Crane> cranes = new ArrayList<>();
        List<Container> containers = new ArrayList<>();

        try {
            File myObj = new File("data/L40_W24_H3_Q1_C3(1).ysi");
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();
            while (myReader.hasNextLine()) {
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

                        slots = new Slot[L/Ls][W/Ws][Hmax];

                        break;
                    case "# slots":
                        data = myReader.nextLine();
                        while (!data.equals("# cranes")) {
                            String[] split3 = data.split(",");
                            Slot slot = new Slot (split3[0], Hmax);
                            slot.setXY(split3[1], split3[2]);
                            slotlist.add(slot);
                            for (int i =0; i<Hmax; i++) {
                                slots[Integer.parseInt(split3[1])/Ls][Integer.parseInt(split3[2])/Ws][i] = slot;
                            }
                            data = myReader.nextLine();
                        }
                        break;
                    case "# cranes":
                        data=myReader.nextLine();
                        while(!data.equals("# containers")) {
                            String[] split4 = data.split(",");
                            cranes.add(new Crane(split4[0], split4[1], split4[2]));
                            data = myReader.nextLine();
                        }
                        break;
                    case "# containers":
                        data = myReader.nextLine();
                        while(!data.equals("# container->slots")) {
                            String [] split5 = data.split(",");
                            containers.add(new Container(split5[0], split5[1], split5[2]));
                            data = myReader.nextLine();
                        }
                        break;
                    case "# container->slots" :
                        while (myReader.hasNextLine()) {
                            data = myReader.nextLine();
                            String [] split6 = data.split(",");
                            Container c = containers.get(Integer.parseInt(split6[0])-1);
                            for (int i = 1; i < split6.length; i++) {
                                Slot s = slotlist.get(Integer.parseInt(split6[i])-1);
                                s.addContainer(c);
                                if (s.getHoogteContainers() > currentHeight) {
                                    currentHeight = s.getHoogteContainers();
                                }
                            }
                        }
                        break;
                }
            }
            myReader.close();

            // labo 1 is gewoon top containers bezoeken
            // dus we nemen gewoon de 1e crane hiervoor

            for (int i = 0; i < slots[0][0].length; i++) {

            }


            // om te checken waar er containers zitten
            System.out.println("slots:");
            for (int i = 0; i < slots[0][0].length; i++) {
                for (int j = 0; j < slots[0].length; j++) {
                    for (int k = 0; k < slots.length; k++) {
                        System.out.print(slots[k][j][i].getId() + " ");
                        System.out.print("("+slots[k][j][i].getContainers()[i]+ ") ");

                    }
                    System.out.println();
                }
                System.out.println();
            }

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
