import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Yard {

    private int L = 0; //totale lengte yard
    private int W = 0; //totale breedte yard
    private int Hmax = 0;

    private int Ls = 0; //lengte slot (10)
    private int Ws = 0; //breedte slot (12)

    private int currentHeight = 0;
    private Slot [][] slots = null;
    private List<Slot> slotlist = new ArrayList<>();
    private List<Container> containers = new ArrayList<>();
    private List<Crane> cranes = new ArrayList<>();

    //gebruikt bij labo 1
    public void moveCrane(){

        //1 kraan gebruiken
        Crane crane = cranes.get(0);

        // Voor elk slot: zoek de hoogste container, bezoek deze indien nog niet bezocht
        boolean[] visited = new boolean[getContainers().size()];

        for (Slot slot : slotlist){

            if (slot.getContainers().size() == 0)
                continue;

            Container hoogsteContainer = slot.getContainers().get(slot.getContainers().size()-1);

            //Bezoek hoogste container indien container nog niet bezocht
            if (!visited[hoogsteContainer.getId()-1]){

                crane.move(hoogsteContainer.bepaalX(), hoogsteContainer.bepaalY(Ws));
                visited[hoogsteContainer.getId()-1] = true;

            }

        }

        //eindpositie (0,0)
        crane.move(0,0);
    }

    // om te checken waar er containers zitten
    public void checkContainers(){
        System.out.println("slots:");
        for (int i = 0; i < Hmax; i++) {
            System.out.println("Hoogte " + i + ":");
            for (int j = 0; j < slots[0].length; j++) {
                for (int k = 0; k < slots.length; k++) {
                    System.out.print(slots[k][j].getId() + " ");
                    System.out.print("("+slots[k][j].getContainers().get(i)+ ") ");

                }
                System.out.println();
            }
        }
    }

    //controleer of Yard voldoet aan veiligheidsvoorschriften, verplaats containers indien dit niet het geval is
    public void checkYard(){

        for (Slot slot : getSlotlist()){
            if (slot.getContainers().size() == 0)
                continue;

            Slot firstSlot = null;
            while(!slot.isSorted())
                firstSlot = findSlots(slot.getContainers().get(slot.getContainers().size()-1));


        }
    }

    // zoek een mogelijk slot voor een container die niet voldoet aan de veiligheidsvoorschriften
    public Slot findSlots(Container container){
        for (int i = 0; i < slots[0].length; i++) {
            for (int j = 0; j < slots.length-container.getSlots().size(); j++) {

                //check max hoogte
                if(slots[j][i].getContainers().size() + 1 > Hmax)
                    continue;

                //check zelfde hoogte
                if(!checkSameHeight(i, j, container)){
                    continue;
                }

                //check sorted
                if (!slots[j][i].isSorted(container))
                    continue;

                //checkWeight
                if(!slots[j][i].checkWeight())
                    continue;

                if (!checkSameLength(i, j, container))
                    continue;

                return slots[j][i];

            }
        }

        return null;
    }

    //verplaats een container naar een nieuwe locatie (geef enkel eerste slot mee, andere slots volgen logisch)
    public void moveContainer(Container container, Slot newSlot){

        Crane crane = cranes.get(0);

        //move crane to new location + pick up container
        crane.moveContainer(container.bepaalX(), container.bepaalY(Ws), container);

        //remove container from slots
        //zoek eerst slot van huidige locatie van container in lijst
        int index = slotlist.indexOf(container.getSlots().get(0));
        //verwijder container uit alle slots
        for (int i = 0; i<container.getSlots().size(); i++){
            slotlist.get(index+i).removeContainer();
        }

        //remove slots from container
        container.getSlots().clear();

        //move crane to new location + drop container
        //middelpunt nieuwe locatie bepalen
        int nextX = newSlot.getX() + container.getLc()/2;
        int nextY = newSlot.getY() + Ws/2;
        crane.moveContainer(nextX, nextY, container);

        //add container to slots
        //zoek slot in lijst
        index = slotlist.indexOf(newSlot);
        for (int i = 0; i<container.getLc()/Ls; i++){
            slotlist.get(index+i).addContainer(container);

            //add slot to container
            container.getSlots().add(slotlist.get(index+i));
        }

    }

    //controleer of alle opeenvolgende slots waar een container in zou geplaatst worden, dezelfde hoogte hebben
    // (container moet op vlak oppervlak staan)
    public boolean checkSameHeight(int i, int j, Container container){
        int hoogte = slots[j][i].getContainers().size();
        for (int k = 0; k < container.getSlots().size(); k++){
            if(hoogte != slots[j][k].getContainers().size())
                return false;
        }
        return true;
    }

    //controleer of de lengte van een container gelijk is aan de lengten van alle onderliggende containers
    // (hoeken van container moeten overeenkomen met hoeken van onderstaande + geen lege slots onder container)
    public boolean checkSameLength(int i, int j, Container container){
        Set<Container> bottomContainers = new HashSet<>();
        for (int k = 0; k < container.getSlots().size(); k++){
            bottomContainers.add(slots[j][k].getContainers().get(slots[j][k].getContainers().size()-1));
        }

        int lengte = 0;
        for (Container container1 : bottomContainers){
            lengte = lengte + container1.getLc();
        }
        if (lengte != container.getLc())
            return false;

        return true;
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
