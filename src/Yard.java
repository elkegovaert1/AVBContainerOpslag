import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Yard {

    private int L = 0;
    private int W = 0;
    private int Hmax = 0;

    private int Ls = 0;
    private int Ws = 0;

    private int currentHeight = 0;
    private Slot [][] slots = null;
    private List<Slot> slotlist = new ArrayList<>();
    private List<Container> containers = new ArrayList<>();
    private List<Crane> cranes = new ArrayList<>();

    // om te checken waar er containers zitten
    public void checkContainers(){
        System.out.println("slots:");
        for (int i = 0; i < Hmax; i++) {
            System.out.println("Hoogte " + i + ":");
            for (int j = 0; j < slots[0].length; j++) {
                for (int k = 0; k < slots.length; k++) {
                    System.out.print(slots[k][j].getId() + " ");
                    System.out.print("("+slots[k][j].getContainers()[i]+ ") ");

                }
                System.out.println();
            }
        }
    }

    public Yard(String pathname) {

        try {
            File myObj = new File(pathname);
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

                        slots = new Slot[L / Ls][W / Ws];

                        break;
                    case "# slots":
                        data = myReader.nextLine();
                        while (!data.equals("# cranes")) {
                            String[] split3 = data.split(",");
                            Slot slot = new Slot(split3[0], Hmax);
                            slot.setXY(split3[1], split3[2]);
                            slotlist.add(slot);
                            slots[Integer.parseInt(split3[1]) / Ls][Integer.parseInt(split3[2]) / Ws] = slot;

                            data = myReader.nextLine();
                        }
                        break;
                    case "# cranes":
                        data = myReader.nextLine();
                        while (!data.equals("# containers")) {
                            String[] split4 = data.split(",");
                            cranes.add(new Crane(split4[0], split4[1], split4[2]));
                            data = myReader.nextLine();
                        }
                        break;
                    case "# containers":
                        data = myReader.nextLine();
                        while (!data.equals("# container->slots")) {
                            String[] split5 = data.split(",");
                            containers.add(new Container(split5[0], split5[1], split5[2]));
                            data = myReader.nextLine();
                        }
                        break;
                    case "# container->slots":
                        while (myReader.hasNextLine()) {
                            data = myReader.nextLine();
                            String[] split6 = data.split(",");
                            Container c = containers.get(Integer.parseInt(split6[0]) - 1);

                            // Voeg container toe aan gegeven slots
                            for (int i = 1; i < split6.length; i++) {
                                Slot s = slotlist.get(Integer.parseInt(split6[i]) - 1);
                                s.addContainer(c);
                                if (s.getHoogteContainers() > currentHeight) {
                                    currentHeight = s.getHoogteContainers();
                                }

                                //Voeg slot toe aan container
                                c.addSlot(s);
                            }
                        }
                        break;
                }
            }

            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeCraneMovements(String fileName){
        try {
            FileWriter myWriter = new FileWriter(fileName);

            myWriter.write("# container->slots\n");
            for (int i = 0; i < Hmax; i++) {
                for (int j = 0; j < slots[0].length; j++) {
                    for (int k = 0; k < slots.length; k++) {
                        if (slots[k][j].getContainers()[i] != null) {
                            myWriter.write(slots[k][j].getContainers()[i].getId() + "," + slots[k][j].getId());
                            int lengte = slots[k][j].getContainers()[i].getLc()/Ls;
                            for (int l =1; l < lengte; l++) {
                                myWriter.write("," + (slots[k][j].getId()+l));
                            }
                            k = k+lengte;
                            myWriter.write("\n");
                        }
                    }
                }
            }
            // labo 1 is gewoon top containers bezoeken
            // dus we nemen gewoon de 1e crane hiervoor
            Crane crane = cranes.get(0);

            myWriter.write("# kraanbewegingen (t,x,y)\n");
            //startpositie
            int t = 0;
            myWriter.write(t + ", " + crane.getX() + ", " + crane.getY() + "\n");

            // Tussenstappen:
            // Voor elk slot: zoek de hoogste container, bezoek deze indien nog niet bezocht
            boolean[] visited = new boolean[containers.size()];

            for (Slot slot : getSlotlist()){
                if (slot.getHoogteContainers() == 0)
                    continue;

                Container hoogsteContainer = slot.getContainers()[slot.getHoogteContainers()-1];

                //Bezoek hoogste container indien container nog niet bezocht
                if (!visited[hoogsteContainer.getId()-1]){

                    //bewegingen in beide richtingen tegelijk => tijd tot volgende container wordt bepaald door maximale afstand tot die container
                    t = t + Math.max(Math.abs(hoogsteContainer.bepaalX() - crane.getX()), Math.abs(hoogsteContainer.bepaalY(getWs()) - crane.getY()));

                    //verplaats kraan naar nieuwe positie
                    crane.setX(hoogsteContainer.bepaalX());
                    crane.setY(hoogsteContainer.bepaalY(getWs()));
                    visited[hoogsteContainer.getId()-1] = true;

                    myWriter.write(t + ", " + crane.getX() + ", " + crane.getY() + "\n");

                }

            }

            //eindpositie (0,0)
            myWriter.write(t + Math.max(crane.getX(), crane.getY()) + ", " + 0 + ", " + 0);

            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public int getL() {
        return L;
    }

    public void setL(int l) {
        L = l;
    }

    public int getW() {
        return W;
    }

    public void setW(int w) {
        W = w;
    }

    public int getHmax() {
        return Hmax;
    }

    public void setHmax(int hmax) {
        Hmax = hmax;
    }

    public int getLs() {
        return Ls;
    }

    public void setLs(int ls) {
        Ls = ls;
    }

    public int getWs() {
        return Ws;
    }

    public void setWs(int ws) {
        Ws = ws;
    }

    public int getCurrentHeight() {
        return currentHeight;
    }

    public void setCurrentHeight(int currentHeight) {
        this.currentHeight = currentHeight;
    }

    public Slot[][] getSlots() {
        return slots;
    }

    public void setSlots(Slot[][] slots) {
        this.slots = slots;
    }

    public List<Slot> getSlotlist() {
        return slotlist;
    }

    public void setSlotlist(List<Slot> slotlist) {
        this.slotlist = slotlist;
    }

    public List<Crane> getCranes() {
        return cranes;
    }

    public void setCranes(List<Crane> cranes) {
        this.cranes = cranes;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }
}
