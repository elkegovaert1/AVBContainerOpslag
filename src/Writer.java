import java.io.FileWriter;
import java.io.IOException;

public class Writer {

    public static void write(String fileName, Yard yard){

        try {
            FileWriter myWriter = new FileWriter(fileName);
            //schrijf #container->slots
            myWriter.write("# container->slots\n");
            for (Container c: yard.getContainers()) {
                myWriter.write(c.getId()+ "," + c.getSlots().get(0).getId());
                for (int i=1; i < c.getSlots().size(); i++) {
                    myWriter.write("," + c.getSlots().get(i).getId());
                }
                myWriter.write("\n");
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
