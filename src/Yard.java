import java.util.*;

public class Yard {

    private int L = 0; //totale lengte yard
    private int W = 0; //totale breedte yard
    private int Hmax = 0;
    private int Hsafe = 0;

    // min and max x value of zone
    private int zoneMin;
    private int zoneMax;

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

                    // restrictiezone
                    if(length == zoneMin/Ls || length == zoneMax/Ls)
                        System.out.print("  ");

                    if(height >= slots[length][width].getContainers().size())
                        System.out.print("__");
                    else{
                        if (Integer.toHexString(slots[length][width].getContainers().get(height).getId()).length() == 1 )
                            System.out.print("0" + Integer.toHexString(slots[length][width].getContainers().get(height).getId()));
                        else
                            System.out.print(Integer.toHexString(slots[length][width].getContainers().get(height).getId()));
                    }
                }
                System.out.println();
            }
        }
    }

    //controleer of elk slot van Yard voldoet aan veiligheidsvoorschriften, verplaats containers indien dit niet het geval is
    public void checkYard(){

        // alle containers sorteren op gewicht (zwaarste bovenaan)
        sortContainers();

        // veranker lichte containers door er zwaardere op te plaatsen
        anchorContainers();

    }

    private void sortContainers() {
        for (Slot slot: slotlist) {

            //indien geen containers => veiligheidsvoorschriften voldaan
            if (slot.getContainers().isEmpty())
                continue;

            // alle containers sorteren op gewicht (zwaarste bovenaan)
            while(!slot.isSorted()){
                Container topContainer = slot.getContainers().get(slot.getContainers().size()-1);
                Slot newSlot = findSlots(topContainer);
                moveContainer(topContainer, newSlot);
                slot = slotlist.get(0);
            }
        }
    }

    private void anchorContainers(){
        for (Slot slot : slotlist){

            //indien geen containers => veiligheidsvoorschriften voldaan
            if (slot.getContainers().isEmpty())
                continue;

            //zolang veiligheidsvoorschriften niet voldaan in slot => zoek mogelijk nieuw slot voor bovenste container
            //kijken ofdat bovenste gewicht niet 1 is
            if (!slot.checkWeight()) {
                // als max hoogte bereikt, eerst container verplaatsen
                if (slot.getContainers().size() == Hsafe) {
                    Container container = slot.getTopContainer();
                    moveContainer(container, findSlots(container));
                    moveHeavierContainer(container);
                    // anders direct zwaardere erop zetten
                } else {
                    moveHeavierContainer(slot.getTopContainer());
                }
            }
        }
    }

    // move container with weight > 1 to container with weight = 1
    public void moveHeavierContainer(Container container) {
        for (Slot slot : getSlotlist()){

            if (checkRestrictionHeavierContainer(slot)) {

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
                    Slot surroundingSlot = checkSurroundingSlots(container, extraSlotsNeeded, container.getSlots().get(0));
                    if (surroundingSlot == null)
                        continue;
                    // verplaats container naar eerste slot van de rijd
                    moveContainer(slot.getContainers().get(slot.getContainers().size()-1), surroundingSlot);
                    return;
                }
            }



        }

        //als geen zwaardere container gevonden is die er automatisch opgeplaatst kan worden
        for (Container c: containers){
            // deel 1: zoek grotere zwaardere container
            if (checkRestrictionHeavierContainer(c.getSlots().get(0))) {
                if (c.getLc() > container.getLc()) {
                    // hoeveel plaatsen nog op te vullen
                    int extraSlotsNeeded = c.getLc() - container.getLc();
                    // vind containers voor er rond en geef eerste slot van de volledige rij terug
                    Container movableContainer = findFittingContainer(container, extraSlotsNeeded);
                    if (movableContainer == null)
                        continue;
                    // als we verplaatsbare container hebben gevonden =>
                    // containers boven de fittingContainer verplaatsen
                    moveContainerAbove(movableContainer);

                    // movableContainer voor of achter plaatsen
                    Slot beginSlotBigContainer = placeMovableContainer(movableContainer, container);
                    if (beginSlotBigContainer == null)
                        continue;

                    // grote container op de containers plaatsen
                    moveContainer(c, beginSlotBigContainer);
                    return;
                }
            }
        }
    }

    private void moveContainerAbove(Container movableContainer) {
        Slot slot = movableContainer.getSlots().get(0);
        int heightMovableContainer = movableContainer.getSlots().get(0).getContainers().indexOf(movableContainer)+1;
        for (int i = heightMovableContainer; i < movableContainer.getSlots().size(); i++) {
            Container toBeReplacedContainer = slot.getContainers().get(i);
            Slot newSlot = findSlots(toBeReplacedContainer);
            moveContainer(toBeReplacedContainer, newSlot);
        }
    }

    private Container findFittingContainer(Container container, int extraSlotsNeeded) {
        for (Container c: containers) {
            // niet dezelfde
            if (container.equals(c)) {
                continue;
            }

            // groot genoeg?
            if (c.getLc() != extraSlotsNeeded)
                continue;

            // er mag geen container met gewicht 1 onder staan
            boolean weightOne = false;
            for (Slot s: c.getSlots()) {
                int heightContainer = s.getContainers().indexOf(c);
                if (heightContainer > 0)
                    if (s.getContainers().get(heightContainer-1).getGc() == 1)
                        weightOne = true;
            }
            if (weightOne)
                continue;

            return c;
        }
        return null;
    }

    private Slot placeMovableContainer(Container movableContainer, Container container) {
        boolean voor = true;
        boolean achter = true;
        Slot beginSlot;
        for (int i = 1; i < movableContainer.getLc()/Ls; i++) {
            // voor
            if (container.getSlots().get(0).getX() - movableContainer.getLc() >= 0) {
                if (!slotlist.get(container.getSlots().get(0).getId()-i).getContainers().isEmpty()) {
                    voor = false;
                }
            } else
                voor = false;

            // achter
            if (container.getSlots().get(container.getSlots().size()-1).getX() + Ls + movableContainer.getLc() <= L) {
                if (!slotlist.get(container.getSlots().get(container.getSlots().size()-1).getId()+i).getContainers().isEmpty()) {
                    achter = false;
                }
            } else
                achter = false;

            if (!voor && !achter)
                return null;
        }
        if (!voor && achter) {
            beginSlot = container.getSlots().get(0);
            moveContainer(movableContainer, slotlist.get(container.getSlots().get(container.getSlots().size()-1).getId()));
        } else {
            beginSlot = slotlist.get(container.getSlots().get(0).getId() - movableContainer.getLc()/Ls);
            moveContainer(movableContainer, slotlist.get(container.getSlots().get(0).getId() - movableContainer.getLc()/Ls));
        }
        return beginSlot;
    }

    private boolean checkRestrictionHeavierContainer(Slot slot) {
        // als geen slot niet checken
        if (slot.getContainers().isEmpty())
            return false;

        // check gewicht > 1
        if (!slot.checkWeight())
            return false;

        // check of eronder niet al 1 of meer containers met gewicht 1 staat
        if (slot.getContainers().size() > 1) {
            boolean movable = true;
            for (int i = 0; i < slot.getTopContainer().getSlots().size(); i++) {
                if ((slot.getTopContainer().getSlots().get(i).getContainers().get(slot.getContainers().size()-2).getGc() == 1)) {
                    movable = false;
                }
            }
            if (!movable) {
                return false;
            }
        }
        return true;
    }

    public Slot checkSurroundingSlots(Container container, int extraSlotsNeeded, Slot slot) {
        Slot firstSlot = slot;
        // before current container
        // als eerste container => overslaan
        if (container.getSlots().get(0).getX() != 0) {
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
        if (container.getSlots().get(0).getX() != L) {
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
            for (int length = 0; length <= slots.length-container.getSlots().size(); length++) {

                //niet hetzelfde slots
                if(container.getSlots().contains(slots[length][width])) {
                    continue;
                }

                //check max hoogte
                if(slots[length][width].getContainers().size() + 1 > Hsafe)
                    continue;

                //check zelfde hoogte
                if(!checkSameHeight(length, width, container))
                    continue;

                //check sorted volledige lijn
                if (!checkSorted(length, width, container))
                    continue;

                //checkWeight volledige lijn
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

        int newLocation = newSlot.getX() + container.getLc()/2;

        //indien 1 kraan niet van container naar bestemming kan, zoek tijdelijke plaats in restrictiezone zodat andere kraan container kan verplaatsen naar bestemming
        if ((container.bepaalX() < zoneMin && newLocation > zoneMax) || (container.bepaalX() > zoneMax && newLocation < zoneMin))
            moveContainerThroughZone(container, newSlot);

        //indien 1 kraan wel van container naar bestemming kan => voer uit
        else {
            //kies juiste kraan (indien container niet in restrictiezone => kies kraan in gebied), indien wel in zone, kies kraan die achterloopt in tijd)
            Crane crane = findCrane(container, newSlot);

            //indien verplaatsing door restrictiezone moet => controleer of andere kraan niet in zone zal zijn dmv tijd en movements
            if (checkTiming(crane, container, newSlot) == 0){
                System.out.println("From: " + container.bepaalX() + " to " + newLocation);
                pickUpContainer(crane, container);
                dropContainer(crane, container, newSlot);
            }
            else{
                //wait
                //pickUpContainer(crane, container);
                //dropContainer(crane, container, newSlot);

            }
        }
    }

    //kies juiste kraan
    private Crane findCrane (Container container, Slot newSlot){
        int newLocation = newSlot.getX() + container.getLc()/2;

        //buiten reservatiezone
        if (container.bepaalX() <= zoneMin)
            return cranes.get(0);
        else if (container.bepaalX() >= zoneMax)
            return cranes.get(1);

        //binnen reservatiezone en verplaatsing uit zone
        else if (container.bepaalX() <= zoneMax && newLocation <= zoneMax)
            return cranes.get(0);
        else if (container.bepaalX() >= zoneMin && newLocation >= zoneMin)
            return cranes.get(1);

        //binnen reservatiezone en verplaatsing binnen zone => kies achterlopende kraan in tijd
        else if (container.bepaalX() <= zoneMax && container.bepaalX() >= zoneMin && newLocation <= zoneMax &&  newLocation >= zoneMin){
            if (cranes.get(0).getCraneRoute().getTime() < cranes.get(1).getCraneRoute().getTime())
                return cranes.get(0);
            else
                return cranes.get(1);
        }

        return null;
    }

    private void moveContainerThroughZone(Container container, Slot newSlot){

        Slot temp = findTemporarySlotInZone(container);
        int a = temp.getX()+container.getLc()/2;
        int b = newSlot.getX()+container.getLc()/2;
        System.out.println("From: " + container.bepaalX() + " to " + a + " to " + b);

        //call moveContainer 2 keer:
        moveContainer(container, temp);

        moveContainer(container, newSlot);

    }

    //zoek plaats in restrictiezone voor doorgeven container
    private Slot findTemporarySlotInZone(Container container){

        //halve container mag buiten zone, enkel middelpunt moet in zone
        int halfContainerSize = container.getSlots().size()/2;

        for (int width = 0; width < slots[0].length; width++) {
            for (int length = zoneMin/Ls-halfContainerSize; length <= zoneMax/Ls-halfContainerSize; length++) {

                System.out.println(length);

                //niet hetzelfde slots => kan nooit door oproepen methode
                //if(container.getSlots().contains(slots[length][width]))
                //    continue;

                //check max hoogte (temporary: Hmax ipv Hsafe)
                if(slots[length][width].getContainers().size() + 1 > Hmax)
                    continue;

                //check zelfde hoogte
                if(!checkSameHeight(length, width, container))
                    continue;

                //temporary => check sorted volledige lijn niet nodig
                //if (!checkSorted(length, width, container))
                //    continue;

                //temporary => checkWeight volledige lijn niet nodig
                //if(!slots[length][width].checkWeight())
                //    continue;

                if (!checkSameLength(length, width, container))
                    continue;

                return slots[length][width];

            }
        }

        return null;
    }

    //TODO: indien verplaatsing door restrictiezone moet => controleer of andere kraan niet in zone zal zijn dmv tijd en movements
    //taak enkel toekennen aan 2e kraan als eerste kraan minstens deze duratie verder zit in tijdlijn??
    private int checkTiming(Crane crane, Container container, Slot newSlot){
        //controleer andere kranen op tijd dat verplaatsing zal gebeuren
        /*
        for (Crane othercrane : getCranes()){
            if (crane == othercrane)
                continue;

        }

         */

        //indien niet door zone => return 0
        return 0;

        //indien wel door zone => return int voor wachttijd
    }

    //move crane to new location + pick up container
    private void pickUpContainer(Crane crane, Container container){

        crane.moveContainer(container.bepaalX(), container.bepaalY(Ws), container);

        //remove container from slots
        //zoek eerste slot van huidige locatie van container in lijst
        int index = slotlist.indexOf(container.getSlots().get(0));
        //verwijder container uit alle slots
        for (int i = 0; i<container.getSlots().size(); i++){
            slotlist.get(index+i).removeContainer();
        }

        //remove slots from container
        container.getSlots().clear();
    }

    //move crane to new location + drop container
    private void dropContainer(Crane crane, Container container, Slot newSlot){
        //middelpunt nieuwe locatie bepalen
        int nextX = newSlot.getX() + container.getLc()/2;
        int nextY = newSlot.getY() + Ws/2;
        crane.moveContainer(nextX, nextY, container);

        //add container to slots
        //zoek eerste slot van nieuwe locatie in lijst
        int index = slotlist.indexOf(newSlot);
        for (int i = 0; i<container.getLc()/Ls; i++){
            Slot slot = slotlist.get(index+i);
            slot.addContainer(container);

            //add slot to container
            container.getSlots().add(slotlist.get(index+i));
        }
    }

    //controleer of alle opeenvolgende slots waar een container in zou geplaatst worden, gesorteerd zou blijven
    private boolean checkSorted(int length, int width, Container container) {
        for (int i = 0; i < container.getSlots().size(); i++){
            if(!slots[length+i][width].isSorted(container))
                return false;
        }
        return true;
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

            // tel alle lengten van onderliggende containers op, indien deze gelijk is aan de lengte van de te plaatsen container is deze check true, anders false
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

    public int getHsafe() {
        return Hsafe;
    }

    public void setHsafe(int hsafe) {
        Hsafe = hsafe;
    }

    public int getZoneMin() {
        return zoneMin;
    }

    public void setZoneMin(int zoneMin) {
        this.zoneMin = zoneMin;
    }

    public int getZoneMax() {
        return zoneMax;
    }

    public void setZoneMax(int zoneMax) {
        this.zoneMax = zoneMax;
    }
}
