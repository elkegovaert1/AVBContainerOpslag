import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {

    public static Yard read(String pathname) {

        Yard yard = new Yard();
        Slot[][] slots = null;

        try {

            File myObj = new File(pathname);
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();
            while (myReader.hasNextLine()) {
                switch (data) {
                    case "# L,W,H_max,H_safe":
                        data = myReader.nextLine();
                        String[] split = data.split(",");

                        yard.setL(Integer.parseInt(split[0]));
                        yard.setW(Integer.parseInt(split[1]));
                        yard.setHmax(Integer.parseInt(split[2]));
                        yard.setHsafe(Integer.parseInt(split[3]));

                        data = myReader.nextLine();
                        break;
                    case "# L_s,W_s":
                        data = myReader.nextLine();
                        String[] split2 = data.split(",");

                        yard.setLs(Integer.parseInt(split2[0]));
                        yard.setWs(Integer.parseInt(split2[1]));
                        slots = new Slot[yard.getL() / yard.getLs()][yard.getW() / yard.getWs()];

                        data = myReader.nextLine();
                        break;
                    case "# slots":
                        data = myReader.nextLine();
                        while (!data.equals("# cranes")) {
                            String[] split3 = data.split(",");
                            Slot slot = new Slot(split3[0]);
                            slot.setXY(split3[1], split3[2]);

                            yard.getSlotlist().add(slot);
                            slots[Integer.parseInt(split3[1]) / yard.getLs()][Integer.parseInt(split3[2]) / yard.getWs()] = slot;

                            data = myReader.nextLine();
                        }
                        break;
                    case "# cranes":
                        data = myReader.nextLine();
                        while (!data.equals("# containers")) {
                            String[] split4 = data.split(",");
                            yard.getCranes().add(new Crane(split4[0], split4[1], split4[2], split4[3], split4[4]));
                            data = myReader.nextLine();
                        }
                        break;
                    case "# containers":
                        data = myReader.nextLine();
                        while (!data.equals("# container -> slots")) { //LABO 2: spaties rond pijlen!
                            String[] split5 = data.split(",");
                            yard.getContainers().add(new Container(split5[0], split5[1], split5[2]));
                            data = myReader.nextLine();
                        }
                        break;
                    case "# container -> slots": //LABO 2: spaties rond pijlen!
                        while (myReader.hasNextLine()) {
                            data = myReader.nextLine();
                            String[] split6 = data.split(",");
                            Container c = yard.getContainers().get(Integer.parseInt(split6[0]) - 1);

                            // Voeg container toe aan gegeven slots
                            for (int i = 1; i < split6.length; i++) {
                                Slot s = yard.getSlotlist().get(Integer.parseInt(split6[i]) - 1);
                                s.addContainer(c);
                                if (s.getContainers().size() > yard.getCurrentHeight()) {
                                    yard.setCurrentHeight(s.getContainers().size());
                                }

                                //Voeg slot toe aan container
                                c.addSlot(s);
                            }
                        }
                        break;
                }
            }
            yard.setSlots(slots);

            if(yard.getCranes().size() == 2){
                yard.setXmax(yard.getCranes().get(0).getXmax());
                yard.setXmin(yard.getCranes().get(1).getXmin());

                for (Crane crane : yard.getCranes()){
                    crane.setZonemin(yard.getXmin());
                    crane.setZonemax(yard.getXmax());
                }
            }

            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return yard;
    }

}
