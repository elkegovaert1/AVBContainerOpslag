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

    // om visueel te checken waar er containers zitten
    public void checkContainers(){
        System.out.println("Yard:[Length = " + slots.length + "*" + Ls + "][Width = " + slots[0].length + "*" + Ws + "]");

        for (int height = 0; height < Hmax; height++) {
            System.out.println("Height " + height + ":");
            for (int width = 0; width < slots[0].length; width++) {
                for (int length = 0; length < slots.length; length++) {
                    //System.out.print(slots[k][j].getId() + " ");
                    if(height >= slots[length][width].getContainers().size())
                        System.out.print("_");
                    else
                        System.out.print(Integer.toHexString(slots[length][width].getContainers().get(height).getId()));

                }
                System.out.println();
            }
        }

        for (Container container : containers)
            System.out.println(container.getId() + " " + container.getGc());
    }

    //controleer of elk slot van Yard voldoet aan veiligheidsvoorschriften, verplaats containers indien dit niet het geval is
    //beweeg naar eindpositie (0,0) zodra alle veiligheidsvoorschriften voldaan zijn
    public void checkYard(){

        for (Slot slot : slotlist){

            // controle of alles gestorteerd + verplaatsen
            controlSorted();

            //indien geen containers => veiligheidsvoorschriften voldaan
            if (slot.getContainers().isEmpty())
                continue;

            //zolang veiligheidsvoorschriften niet voldaan in slot => zoek mogelijk nieuw slot voor bovenste container
            //kijken ofdat bovenste gewicht niet 1 is
            if (!slot.checkWeight()) {
                // als max hoogte bereikt, eerst container verplaatsen
                if (slot.getContainers().size() == Hmax) {
                    Container container = slot.getTopContainer();
                    moveContainer(container, findSlots(container));
                    moveHeavierContainer(container);
                // anders direct zwaardere erop zetten
                } else {
                    moveHeavierContainer(slot.getTopContainer());
                }

            }

        }

        //eindpositie (0,0)
        cranes.get(0).move(0,0);

    }

    private void controlSorted() {
        for (Slot slot: slotlist) {
            if (slot.getContainers().isEmpty())
                continue;

            while(!slot.isSorted()){
                Container topContainer = slot.getContainers().get(slot.getContainers().size()-1);
                Slot newSlot = findSlots(topContainer);
                moveContainer(topContainer, newSlot);
                slot = slotlist.get(0);
            }
        }
    }

    // move container with weight > 1 to container with weight = 1
    public void moveHeavierContainer(Container container) {
        for (Slot slot : getSlotlist()){
            // als geen slot niet checken
            if (slot.getContainers().isEmpty())
                continue;

            // check gewicht > 1
            if (!slot.checkWeight())
                continue;

            // check of eronder niet al 1 of meer containers met gewicht 1 staat
            if (slot.getContainers().size() > 1) {
                boolean movable = true;
                for (int i = 0; i < slot.getTopContainer().getSlots().size(); i++) {
                    if ((slot.getTopContainer().getSlots().get(i).getContainers().get(slot.getContainers().size()-2).getGc() == 1)) {
                        movable = false;
                    }
                }
                if (!movable) {
                    continue;
                }
            }

            // check grootte is gelijk
            if (slot.getContainers().get(slot.getContainers().size()-1).getLc() == container.getLc()) {
                moveContainer(slot.getContainers().get(slot.getContainers().size()-1), container.getSlots().get(0));
                return;
            }

            // check grootte is groter
            if (slot.getContainers().get(slot.getContainers().size()-1).getLc() > container.getLc()) {
                // hoeveel plaatsen nog op te vullen
                int extraSlotsNeeded = slot.getContainers().get(slot.getContainers().size()-1).getLc() - container.getLc();
                // vind containers voor er rond en geef eerste slot van de volledige rij terug
                Slot newSlot = checkSurroundingSlots(container, extraSlotsNeeded, container.getSlots().get(0));
                if (newSlot == null)
                    continue;
                // verplaats container naar eerste slot van de rijd
                moveContainer(slot.getContainers().get(slot.getContainers().size()-1), newSlot);
                return;
            }

        }

    }

    public Slot checkSurroundingSlots(Container container, int extraSlotsNeeded, Slot slot) {
        Slot firstSlot = slot;
        // before current container
        // als eerste container => overslaan
        if (container.getSlots().get(container.getSlots().get(0).getId()-1).getId() > 1) {
            // als geen containers voor => overslaan
            if (!slotlist.get(container.getSlots().get(0).getId()-1).getContainers().isEmpty()) {
                Slot slotBefore = slotlist.get(container.getSlots().get(0).getId()-1);
                // check same height
                if (slotBefore.getContainers().size() == container.getSlots().get(0).getContainers().size()) {
                    extraSlotsNeeded = extraSlotsNeeded - slotBefore.getTopContainer().getLc();
                    // als 0 dan volledig ondersteund
                    if (extraSlotsNeeded == 0) {
                        return slotBefore.getTopContainer().getSlots().get(0);
                    }
                    // als > 0 dan nog extra containers nodig
                    else if (extraSlotsNeeded > 0) {
                        firstSlot = slotBefore.getTopContainer().getSlots().get(0);
                        return checkSurroundingSlots(slotBefore.getTopContainer(), extraSlotsNeeded, firstSlot);
                    }
                    // corrigeer voor volgende stap
                    else
                        extraSlotsNeeded = extraSlotsNeeded + slotlist.get(container.getSlots().get(0).getId()-1).getTopContainer().getLc();
                }
            }
        }

        // after current container
        // als laatste container => overslaan
        if (container.getSlots().get(container.getSlots().size()-1).getId() != slotlist.size()) {
            // als geen containers erna => overslaan
            if (!slotlist.get(container.getSlots().get(container.getSlots().size()-1).getId()+1).getContainers().isEmpty()) {
                Slot slotAfter = slotlist.get(container.getSlots().get(container.getSlots().size()-1).getId()+1);
                // check same height
                if (slotAfter.getContainers().size() == container.getSlots().get(0).getContainers().size()) {
                    extraSlotsNeeded = extraSlotsNeeded - slotAfter.getTopContainer().getLc();
                    // als 0 dan volledig ondersteund
                    if (extraSlotsNeeded == 0)
                        return slotAfter.getTopContainer().getSlots().get(0);
                    // als > 0 dan nog extra containers nodig
                    else if (extraSlotsNeeded > 0) {
                        return checkSurroundingSlots(slotAfter.getTopContainer(), extraSlotsNeeded, firstSlot);
                    }
                }
            }
        }
        // geen passende combinatie gevonden
        return null;
    }

    // zoek een mogelijk slot voor een container die niet voldoet aan de veiligheidsvoorschriften
    public Slot findSlots(Container container){
        for (int width = 0; width < slots[0].length; width++) {
            for (int length = 0; length < slots.length-container.getSlots().size(); length++) {

                //check max hoogte
                if(slots[length][width].getContainers().size() + 1 > Hmax)
                    continue;

                //check zelfde hoogte
                if(!checkSameHeight(length, width, container))
                    continue;

                //check sorted
                if (!slots[length][width].isSorted(container))
                    continue;

                //checkWeight
                if(!slots[length][width].checkWeight())
                    continue;

                if (!checkSameLength(length, width, container))
                    continue;

                return slots[length][width];

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
            Slot slot = slotlist.get(index+i);
            slot.addContainer(container);
            //slotlist.get(index+i).addContainer(container);

            //add slot to container
            container.getSlots().add(slotlist.get(index+i));
        }

    }

    //controleer of alle opeenvolgende slots waar een container in zou geplaatst worden, dezelfde hoogte hebben
    // (container moet op vlak oppervlak staan)
    public boolean checkSameHeight(int length, int width, Container container){
        int height = slots[length][width].getContainers().size();
        for (int i = 0; i < container.getSlots().size(); i++){
            if(height != slots[length+i][width].getContainers().size())
                return false;
        }
        return true;
    }

    //controleer of de lengte van een container gelijk is aan de lengten van alle onderliggende containers
    // (hoeken van container moeten overeenkomen met hoeken van onderstaande + geen lege slots onder container)
    public boolean checkSameLength(int length, int width, Container container){

        //indien container op de grond staat, geen probleem met lengte
        if (slots[length][width].getContainers().size() == 0)
            return true;

        // indien er onderstaande containers zijn
        else{
            //set van containers die zich zouden bevinden onder de container die we willen plaatsen
            Set<Container> bottomContainers = new HashSet<>();

            for (int i = 0; i < container.getSlots().size(); i++){
                List<Container> stack = slots[length+i][width].getContainers();
                //bovenste container van huidige stapel toevoegen
                bottomContainers.add(stack.get(stack.size()-1));
            }

            // tel alle lengten van onderliggende containers op, indien deze gelijk is aan de lengte van de te plaasten container is deze check true, anders false
            int totalLength = 0;
            for (Container c : bottomContainers){
                totalLength = totalLength + c.getLc();
            }
            if (totalLength != container.getLc())
                return false;

            return true;
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
