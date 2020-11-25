import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
            myWriter.write("# kraanbewegingen (t,q_id,x,y[,c_id])\n");

            //tijd moet chronologisch voor alle kranen
            ArrayList<CraneMovement> movements = new ArrayList<>();
            for (Crane crane : yard.getCranes())
                movements.addAll(crane.getCraneRoute().getMovements());

            movements.sort(Comparator.comparingInt(CraneMovement::getTime));

            for (CraneMovement movement : movements)
                myWriter.write(movement.toString());

            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
