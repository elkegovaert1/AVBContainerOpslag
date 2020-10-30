import java.io.FileWriter;
import java.io.IOException;

public class Writer {

    public static void write(String fileName, Yard yard){

        try {
            FileWriter myWriter = new FileWriter(fileName);

            //schrijf #container->slots
            myWriter.write("# container->slots\n");
            for (int i = 0; i < yard.getHmax(); i++) {
                for (int j = 0; j < yard.getSlots()[0].length; j++) {
                    for (int k = 0; k < yard.getSlots().length; k++) {
                        if (yard.getSlots()[k][j].getContainers().size() > i) {
                            myWriter.write(yard.getSlots()[k][j].getContainers().get(i).getId() + "," + yard.getSlots()[k][j].getId());
                            int lengte = yard.getSlots()[k][j].getContainers().get(i).getLc()/yard.getLs();
                            for (int l =1; l < lengte; l++) {
                                myWriter.write("," + (yard.getSlots()[k][j].getId()+l));
                            }
                            k = k+lengte;
                            myWriter.write("\n");
                        }
                    }
                }
            }

            //schrijf kraanbewegingen voor elke kraan
            myWriter.write("# kraanbewegingen (t,x,y)\n");

            for (Crane crane : yard.getCranes())
                for (String movement : crane.getCraneRoute().getMovements())
                    myWriter.write(movement);

            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
