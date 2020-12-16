import java.util.*;

public class Yard {

    private static int L = 0; //totale lengte yard
    private static int W = 0; //totale breedte yard
    private int Hmax = 0;
    private int Hsafe = 0;

    private int Ls = 0; //lengte slot (10)
    private int Ws = 0; //breedte slot (12)

    private int currentHeight = 0;
    private Slot [][] slots = null;
    private List<Slot> slotlist = new ArrayList<>();
    private List<Container> containers = new ArrayList<>();
    private List<Crane> cranes = new ArrayList<>();

    //--------------------------------------------- VISUAL CHECK OF CONTAINERS IN YARD------------------------------
    public void checkContainers(){
        System.out.println("Yard:[Length = " + slots.length + "*" + Ls + "][Width = " + slots[0].length + "*" + Ws + "]");

        for (int height = 0; height < Hmax; height++) {
            System.out.println("Height " + height + ":");
            for (int width = 0; width < slots[0].length; width++) {
                for (int length = 0; length < slots.length; length++) {

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



    //--------------------------------------------- HIGH LEVEL METHOD FOR SORTINGS YARD ----------------------------
    //controleer of elk slot van Yard voldoet aan veiligheidsvoorschriften, verplaats containers indien dit niet het geval is
    public void checkYard(){

        // alle containers sorteren op gewicht (zwaarste bovenaan)
        sortContainers();

        // veranker lichte containers door er zwaardere op te plaatsen
        anchorContainers();

    }



    //--------------------------------------------- METHODS FOR INITIAL SORTING YARD ------------------------------
    // PROVIDES FOLLOWING SAFETY RESTRICTIONS:
    // - Every stack is sorted on weight with heaviest container on top
    // - Every stack is maximum Hsafe hight
    // PHYSICAL RESTRICTIONS ARE ALWAYS RESPECTED

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

    // finds a new location for a container, returns only the first slot (the other slots follow logically)
    // search on same length first to avoid unnecessary movements in length that could be blocked by other cranes
    public Slot findSlots(Container container){
        for (int length = 0; length <= slots.length-container.getSlots().size(); length++) {
            for (int width = 0; width < slots[0].length; width++) {

                //niet hetzelfde slots
                if(container.getSlots().contains(slots[length][width]))
                    continue;

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



    //--------------------------------------------- METHODS FOR ANCHORING CONTAINERS -----------------------------
    // PROVIDES FOLLOWING SAFETY RESTRICTIONS:
    // - THE WEIGHT OF THE TOP CONTAINERS MUST BE LARGER THAN 1
    // - CONTAINERS ARE CHOSEN SO OTHER SAFETY RESTRICTIONS WILL STAY RESPECTED
    // PHYSICAL RESTRICTIONS ARE ALWAYS RESPECTED

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



    //--------------------------------------------- METHODS FOR CRANE INTERFERENCE ------------------------------

    //move a container to a given location (pass only first slot, other slots follow logically)
    public void moveContainer(Container container, Slot newSlot){

        int pickupLocation = container.bepaalX();
        int dropLocation = newSlot.getX() + container.getLc()/2;

        //check which cranes can move from start to finish in 1 movement (no temporary slots required)
        List<Crane> possibleCranes = new ArrayList<>();
        for (Crane crane: cranes)
            if (pickupLocation > crane.getMaxLeft() && pickupLocation < crane.getMaxRight() && dropLocation > crane.getMaxLeft() && dropLocation < crane.getMaxRight())
                possibleCranes.add(crane);

        //if not possible for any crane to go from pickup to dropoff => find temporary slot (only used )
        if (possibleCranes.isEmpty())
            moveContainerWithIntermediateStep(container, newSlot);
        else{
            //indien 1 kraan wel van container naar bestemming kan => find the best crane to perform the movement
            Crane crane = findCrane(container, newSlot, possibleCranes);


            checkCraneInterference(crane, pickupLocation);
            checkMovementSequence(crane, container);
            pickUpContainer(crane, container);


            checkCraneInterference(crane, dropLocation);
            checkMovementSequence(crane, container);
            dropContainer(crane, container, newSlot);
        }

    }

    private void checkCraneInterference(Crane crane, int newLocation){

        //check if neighbouring crane has to move
        int index = cranes.indexOf(crane);

        //if there is a leftneighbour and crane has to move left
        if (index > 0 && newLocation < crane.getX()){
            Crane leftNeighbour = cranes.get(index-1);

            // wacht tot einde beweging om te checken of er interference is
            if (crane.getCraneRoute().getTime() < leftNeighbour.getCraneRoute().getTime())
                crane.wait(leftNeighbour.getCraneRoute().getTime());
            if (crane.getCraneRoute().getTime() > leftNeighbour.getCraneRoute().getTime())
                leftNeighbour.wait(crane.getCraneRoute().getTime());

            int currentTime = crane.getCraneRoute().getTime();

            int a = newLocation;
            int b = leftNeighbour.getCraneZoneMax(currentTime);
            int c = crane.getCraneZoneMin(currentTime);

            //(newLocation < leftNeighbour < currentCrane)
            if (newLocation <= leftNeighbour.getCraneZoneMax(currentTime) && leftNeighbour.getCraneLocation(currentTime) <= crane.getCraneLocation(currentTime)){
                //move othercrane left, returns when othercrane starts movement
                moveInterferingCrane(leftNeighbour, newLocation - Math.max(crane.getXsafe(), leftNeighbour.getXsafe()));
            }


        }

        //if there is a rightneighbour and crane has to move right
        if(index < cranes.size()-1 && crane.getX() < newLocation){
            Crane rightNeighbour = cranes.get(index+1);

            // wacht tot einde beweging om te checken of er interference is
            if (crane.getCraneRoute().getTime() < rightNeighbour.getCraneRoute().getTime())
                crane.wait(rightNeighbour.getCraneRoute().getTime());
            if (crane.getCraneRoute().getTime() > rightNeighbour.getCraneRoute().getTime())
                rightNeighbour.wait(crane.getCraneRoute().getTime());

            int currentTime = crane.getCraneRoute().getTime();

            //(currentCrane < rightNeighbour < newLocation)
            if (crane.getCraneLocation(currentTime) < rightNeighbour.getCraneLocation(currentTime) && rightNeighbour.getCraneZoneMin(currentTime) <= newLocation){
                //move othercrane right, returns when othercrane starts movement
                moveInterferingCrane(rightNeighbour, newLocation + Math.max(crane.getXsafe(), rightNeighbour.getXsafe()));
            }
        }

    }

    //move a crane to a new x value
    public void moveInterferingCrane(Crane crane, int newLocation){

        // kraan moet minstens tot huidige tijd wachten om zelf te kunnen bewegen
        int craneTime = crane.getCraneRoute().getTime();

        checkCraneInterference(crane, newLocation);

        //move crane out of the way
        crane.move(newLocation, crane.getY());

    }

    private void checkMovementSequence(Crane crane, Container container){
        int lastChange = 0;
        for(Slot slot : container.getSlots())
            lastChange = Math.max(lastChange, slot.getLastChange());

        // wait until change to slot happens
        if (lastChange > crane.getCraneRoute().getTime()){
            crane.wait(lastChange);
        }
    }

    private Crane findCrane (Container container, Slot newSlot, List<Crane> possibleCranes){

        //if only one crane, choose that crane
        if (cranes.size() == 1)
            return cranes.get(0);

        int pickupLocation = container.bepaalX();
        int dropLocation = newSlot.getX() + container.getLc()/2;

        //if exactly one crane can do entire movement, choose that crane
        if (possibleCranes.size() == 1)
            return possibleCranes.get(0);

        //TODO: replace random crane with better choice
        Crane crane = possibleCranes.get(new Random().nextInt(possibleCranes.size()));

        //check that the chosen crane will not cross the last known location of neighbouring cranes
        //determine place of crane in cranes
        int i=0;
        while (crane != cranes.get(i))
            i++;

        Crane leftNeighbourCrane = null;
        Crane rightNeighbourCrane = null;

        //cranes on left side of yard have no left neighbour
        if (i == 0)
            rightNeighbourCrane = cranes.get(i+1);

        else if (i == cranes.size()-1)
            leftNeighbourCrane = cranes.get(i-1);

        // left and right neighbour
        else {
            rightNeighbourCrane = cranes.get(i+1);
            leftNeighbourCrane = cranes.get(i-1);
        }

        //check conflicts
        boolean conflictingLeftNeighbour;
        if (leftNeighbourCrane == null)
            conflictingLeftNeighbour = false;
        else
            conflictingLeftNeighbour = conflictingLeftNeighbour(leftNeighbourCrane, crane, pickupLocation, dropLocation);

        boolean conflictingRightNeighbour;
        //if no right neighbour => no conflict
        if (rightNeighbourCrane == null)
            conflictingRightNeighbour = false;
        else
            conflictingRightNeighbour = conflictingRightNeighbour(rightNeighbourCrane, crane, pickupLocation, dropLocation);

        //both not left conflicting
        if (!conflictingLeftNeighbour && !conflictingRightNeighbour)
            return crane;
        //only left conflicting
        else if (conflictingLeftNeighbour && !conflictingRightNeighbour)
            return leftNeighbourCrane;
        //only right conflicting
        else if (!conflictingLeftNeighbour)
            return rightNeighbourCrane;
        //both conflicting
        else{
            //TODO: replace random crane with better choice
            if (new Random().nextBoolean())
                return leftNeighbourCrane;
            else
                return rightNeighbourCrane;
        }
    }

    private boolean conflictingLeftNeighbour(Crane leftNeighbourCrane, Crane crane, int pickupLocation, int dropLocation){

        int closestPossibleLocationToLeftNeighbour = leftNeighbourCrane.getX()+Math.max(leftNeighbourCrane.getXsafe(), crane.getXsafe());

        //if neighbour is ahead in timeline => no problem to compute locations
        if (leftNeighbourCrane.getCraneRoute().getTime() >= crane.getCraneRoute().getTime())
            return false;
        //if pickup and drop locations do not cross last known location zone => no problem
        else if (pickupLocation > closestPossibleLocationToLeftNeighbour && dropLocation > closestPossibleLocationToLeftNeighbour)
            return false;
        //don't cross last location of other cranes that are behind in timeline => pick other crane (rightneighbour)
        else{
            //TODO: possible loop with switching cranes?
            return true;
        }
    }

    private boolean conflictingRightNeighbour(Crane rightNeighbourCrane, Crane crane, int pickupLocation, int dropLocation){

        int closestPossibleLocationToRightNeighbour = rightNeighbourCrane.getX()-Math.max(rightNeighbourCrane.getXsafe(), crane.getXsafe());

        //if neighbour is ahead in timeline => no problem to compute locations
        if (rightNeighbourCrane.getCraneRoute().getTime() >= crane.getCraneRoute().getTime())
            return false;
        //if pickup and drop locations do not cross last known location zone => no problem
        else if (pickupLocation < closestPossibleLocationToRightNeighbour && dropLocation < closestPossibleLocationToRightNeighbour)
            return false;
        //don't cross last location of other cranes that are behind in timeline => pick other crane (rightneighbour)
        else{
            //TODO: possible loop with switching cranes?
            return true;
        }
    }

    //move crane to new location + pick up container
    private void pickUpContainer(Crane crane, Container container){

        crane.moveContainer(container.bepaalX(), container.bepaalY(Ws), container);
        int time = crane.getCraneRoute().getTime();

        //remove container from slots
        //zoek eerste slot van huidige locatie van container in lijst
        int index = slotlist.indexOf(container.getSlots().get(0));
        //verwijder container uit alle slots
        for (int i = 0; i<container.getSlots().size(); i++){
            slotlist.get(index+i).removeContainer(time);
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
        int time = crane.getCraneRoute().getTime();

        //add container to slots
        //zoek eerste slot van nieuwe locatie in lijst
        int index = slotlist.indexOf(newSlot);
        for (int i = 0; i<container.getLc()/Ls; i++){
            Slot slot = slotlist.get(index+i);
            slot.addContainer(container, time);

            //add slot to container
            container.getSlots().add(slotlist.get(index+i));
        }
    }

    //zoek plaats in restrictiezone voor doorgeven container
    private Slot findTemporarySlot(Container container, Slot newSlot){

        //determine lowest maxRight(when mov) or highest maxLeft as bounds for searching
        int lowestMaxRight = L;
        int highestMaxLeft = 0;
        for (Crane crane : cranes){
            lowestMaxRight = Math.min(lowestMaxRight, crane.getMaxRight());
            highestMaxLeft = Math.max(highestMaxLeft, crane.getMaxLeft());
        }

        //halve container mag buiten zone, enkel middelpunt moet in zone
        int halfContainerSize = container.getSlots().size()/2;

        for (int width = 0; width < slots[0].length; width++) {
            for (int length = highestMaxLeft/Ls-halfContainerSize; length <= lowestMaxRight/Ls-halfContainerSize; length++) {

                //check max hoogte (temporary: Hmax ipv Hsafe)
                if(slots[length][width].getContainers().size() + 1 > Hmax)
                    continue;

                //check zelfde hoogte
                if(!checkSameHeight(length, width, container))
                    continue;

                if (!checkSameLength(length, width, container))
                    continue;

                //check if container can be moved in to destination in 1 step if this slot is used as intermediate
                int pickupLocation = slots[length][width].getX() + container.getLc()/2;
                int dropLocation = newSlot.getX() + container.getLc()/2;

                //check which cranes can move from start to finish in 1 movement (no temporary slots required)
                List<Crane> possibleCranes = new ArrayList<>();
                for (Crane crane: cranes)
                    if (pickupLocation > crane.getMaxLeft() && pickupLocation < crane.getMaxRight() && dropLocation > crane.getMaxLeft() && dropLocation < crane.getMaxRight())
                        possibleCranes.add(crane);

                //if not possible for any crane to go from pickup to dropoff => keep searching
                if (!possibleCranes.isEmpty())
                    return slots[length][width];


            }
        }

        return null;
    }

    private void moveContainerWithIntermediateStep(Container container, Slot newSlot){

        moveContainer(container, findTemporarySlot(container, newSlot));
        moveContainer(container, newSlot);

    }

    //calculate effective borders for every crane in the yard (maxLeft and maxRight)
    public void calculateEffectiveCraneBorders(){

        //sort cranes on initial x value (cranes can never escape this order)
        //(for safety, this order can possibly be assumed from the inputfiles)
        Collections.sort(cranes);

        //maxLeft
        //most left crane can go to border
        cranes.get(0).setMaxLeft(0);

        //other cranes can at maximum go to the largest safety distance of the left crane and this crane
        for (int i=1; i<cranes.size(); i++)
            cranes.get(i).setMaxLeft(cranes.get(i-1).getMaxLeft() + Math.max(cranes.get(i-1).getXsafe(), cranes.get(i).getXsafe()) + 1);

        //maxRight
        //most right crane can go to border
        cranes.get(cranes.size()-1).setMaxRight(L);

        //other cranes can at maximum go to the largest safety distance of the right crane and this crane
        for (int i=cranes.size()-2; i>=0; i--)
            cranes.get(i).setMaxRight(cranes.get(i+1).getMaxRight() - Math.max(cranes.get(i+1).getXsafe(), cranes.get(i).getXsafe()) - 1);


    }


    //--------------------------------------------- OLD METHODS: LAB 3 ---------------------------------------------

    /*
    // min and max x value of zone
    private int zoneMin;
    private int zoneMax;
    */

    /*
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
    */

    /*
    //verplaats een container naar een nieuwe locatie (geef enkel eerste slot mee, andere slots volgen logisch)
    public void moveContainer(Container container, Slot newSlot){

        // fout ontdekt door example2.ysi te runnen

        int newLocation = newSlot.getX() + container.getLc()/2;

        //indien 1 kraan niet van container naar bestemming kan, zoek tijdelijke plaats in restrictiezone zodat andere kraan container kan verplaatsen naar bestemming
        if ((container.bepaalX() < zoneMin && newLocation > zoneMax) || (container.bepaalX() > zoneMax && newLocation < zoneMin))
            moveContainerThroughZone(container, newSlot);

            //indien 1 kraan wel van container naar bestemming kan => voer uit
        else {
            //kies juiste kraan (indien container niet in restrictiezone => kies kraan in gebied), indien wel in zone, kies kraan die achterloopt in tijd)
            Crane crane = findCrane(container, newSlot);

            //indien verplaatsing door restrictiezone moet => controleer of andere kraan niet in zone zal zijn dmv tijd en movements
            int waitTime = checkTiming(crane, container, newSlot);
            //System.out.println("From: " + container.bepaalX() + " to " + newLocation + " with waiting time " + waitTime + " by crane " + crane.getId());
            if (waitTime == 0){
                //System.out.println("From: " + container.bepaalX() + " to " + newLocation);

                //controle dat als we een container verplaatsen dat een andere crane niet bezig is met deze te verplaatsen op die moment
                // => controleer of tijd van de kraan achter tijd van laatste aanpassing aan slot komt => container zal zeker neergeplaatst zijn voordat andere kraan hem oppikt
                int lastChange = 0;
                for(Slot slot : container.getSlots())
                    lastChange = Math.max(lastChange, slot.getLastChange());

                if (lastChange > crane.getCraneRoute().getTime()){
                    crane.getCraneRoute().setTime(container.getSlots().get(0).getLastChange()); //wait
                    crane.move(crane.getX(), crane.getY());
                }

                pickUpContainer(crane, container);
                dropContainer(crane, container, newSlot);
            }
            else{
                // => controleer of tijd van de kraan achter tijd van laatste aanpassing aan slot komt => container zal zeker neergeplaatst zijn voordat andere kraan hem oppikt
                int lastChange = 0;
                for(Slot slot : container.getSlots())
                    lastChange = Math.max(lastChange, slot.getLastChange());

                int craneTime = Math.max(crane.getCraneRoute().getTime() + waitTime, lastChange);
                crane.getCraneRoute().setTime(craneTime); //wait

                crane.move(crane.getX(), crane.getY());
                pickUpContainer(crane, container);
                dropContainer(crane, container, newSlot);

            }
        }
    }
    */

    /*
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
    */

    /*
    private void moveContainerThroughZone(Container container, Slot newSlot){

        Slot temp = findTemporarySlotInZone(container);
        int a = temp.getX()+container.getLc()/2;
        int b = newSlot.getX()+container.getLc()/2;
        //System.out.println("From: " + container.bepaalX() + " to " + a + " to " + b);

        //call moveContainer 2 keer:

        moveContainer(container, temp);

        moveContainer(container, newSlot);

    }
    */

    /*
    //zoek plaats in restrictiezone voor doorgeven container
    private Slot findTemporarySlotInZone(Container container){

        //halve container mag buiten zone, enkel middelpunt moet in zone
        int halfContainerSize = container.getSlots().size()/2;

        for (int width = 0; width < slots[0].length; width++) {
            for (int length = zoneMin/Ls-halfContainerSize; length <= zoneMax/Ls-halfContainerSize; length++) {

                //System.out.println(length);

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
    */

    /*
    //taak enkel toekennen aan 2e kraan als eerste kraan minstens deze duratie verder zit in tijdlijn??
    private int checkTiming(Crane crane, Container container, Slot newSlot){
        // wanneer is kraan aan de restrictiezone? + komt hij er zelf in?
        int whenInZone = 0;
        int totalTimeToBorder = 0;
        if (crane.getX() < zoneMax && crane.getY() > zoneMin) {
            return 0; // kunnen nooit 2 cranes in restriction zone zitten
        } else if (container.bepaalX() < zoneMax && container.bepaalX() > zoneMin) {
            int timeToRestrictionBorder = timeToRestrictionBorder(container, newSlot, "in"); // recht naar container => wanneer kruising?
            totalTimeToBorder = timeToRestrictionBorder;
            whenInZone = crane.getCraneRoute().getTime() + timeToRestrictionBorder;
        } else if (newSlot.getX() < zoneMax && newSlot.getX() > zoneMin) { // eerst naar container, DAN naar slot => wanneer kruising?
            int timeToContainer = Math.max(Math.abs(crane.getX() - container.bepaalX()), Math.abs(crane.getY() - container.bepaalY(container.getLc())));
            int timeToRestrictionBorder = timeToRestrictionBorder(container, newSlot, "in");
            totalTimeToBorder = timeToContainer + timeToRestrictionBorder;
            whenInZone = crane.getCraneRoute().getTime() + timeToContainer + timeToRestrictionBorder;
        } else {
            return 0;
        }

        int totalTimeToBorder = timeToContainer + timeToRestrictionBorder;

        //controleer andere kranen op tijd dat verplaatsing zal gebeuren
        for (Crane othercrane : getCranes()){
            if (crane == othercrane)
                continue;

            // wat is de huidige movement
            List<CraneMovement> craneMovements = othercrane.getCraneRoute().getMovements();
            CraneMovement currentOtherCraneMovement = null;
            for (CraneMovement cm: craneMovements) {
                if (cm.getTime() >= whenInZone) {
                    currentOtherCraneMovement = cm;
                    break;
                }
            }

            // als andere kraan nog nie zo ver
            if (currentOtherCraneMovement == null) {
                continue;
            }

            // als wel zo ver, waar is kraan tijdens de beweging
            // als NAAR de zone op het huidige moment
            CraneMovement movementOutOfZone;
            if (currentOtherCraneMovement.getNextX() > zoneMin && currentOtherCraneMovement.getNextX() < zoneMax) {
                int index = craneMovements.indexOf(currentOtherCraneMovement);
                movementOutOfZone = craneMovements.get(index+1);
                // als IN zone op het huidige moment:
            } else if (currentOtherCraneMovement.getX() > zoneMin && currentOtherCraneMovement.getX() < zoneMax) {
                // als de kraan naar buiten gaat in de beweging
                movementOutOfZone = currentOtherCraneMovement;
            } else { // geen probleem
                continue;
            }

            // als kraan van de 1e keer naar buiten gaat
            if (currentOtherCraneMovement.getNextX() < zoneMin || currentOtherCraneMovement.getNextX() > zoneMax) {
                int t = timeToRestrictionBorder(container,newSlot,"out") - totalTimeToBorder;

                //System.out.println("Kraan kan van 1ste keer direct naar buiten gaan met " + t);
                //System.out.println("Crane is: " + crane.getX() + " | Other is: " + othercrane.getX());
                //System.out.println();

                if (t < 0) {
                    return 0;
                } else {
                    return t;
                }

            } else { // als kraan nie direct naar buiten gaat
                // kijk naar kraan beweging tot hij eruit gaat, als einde in restriction zone is => naar buiten bewegen
                boolean stop = false;
                int startTime = currentOtherCraneMovement.getTime();
                while (!stop) {
                    if (movementOutOfZone.getNextX() > zoneMax || movementOutOfZone.getNextX() < zoneMin) {
                        stop = true;
                    } else {
                        int i = craneMovements.indexOf(movementOutOfZone);
                        if (craneMovements.size()-1 == i) { // op einde van bewegingen
                            movementOutOfZone = null;
                            stop = true;
                        } else {
                            movementOutOfZone = craneMovements.get(i + 1); // volgende movement
                        }
                    }
                }

                if (movementOutOfZone == null) { // als er geen volgende bewegingen maar zijn => ga uit restriction zone
                    // tijd van verplaatsing othercrane uit zone - tijd van currentcrane in de zone te gaan
                    int timeToWait = moveCraneOutOfZone(othercrane) - totalTimeToBorder;
                    //System.out.println("Kraan " + othercrane.getId() +" gaat vrijwillig naar buiten met wait: " + timeToWait);
                    //System.out.println();

                    if (timeToWait < 0) {
                        return 0;
                    } else {
                        return timeToWait;
                    }
                } else {
                    int time = timeToRestrictionBorderMovement(movementOutOfZone, "out");
                    int timePassed = movementOutOfZone.getTime() - startTime;
                    //System.out.println("Kraan " + othercrane.getId() + " kan na enkele stappen naar buiten: " + (timePassed + time));
                    //System.out.println();

                    return timePassed + time; // tijd vanaf we erin willen tot hij er effectief uit is

                }
            }
        }

        //indien niet door zone => return 0
        return 0;

        //indien wel door zone => return int voor wachttijd
    }
         */

    /*
    private int timeToRestrictionBorderMovement(CraneMovement cm, String inOrOutZone) {

        int currentX = cm.getX();
        int currentY = cm.getY();

        int endX = cm.getNextX();
        int endY = cm.getNextY();

        int zoneX = 0;

        if (inOrOutZone.equals("in")) {
            if (currentX < zoneMin) {
                zoneX = zoneMin;
            } else if (currentX > zoneMax) {
                zoneX = zoneMax;
            }
        } else { // out
            if (endX < zoneMin) {
                zoneX = zoneMin;
            } else if (endX > zoneMax) {
                zoneX = zoneMax;
            }
        }

        // snijpunt met restrictie zone
        int zoneY = ((endY-currentY)/(endX-currentX))*(zoneX-currentX) + currentY;
        if (zoneY < 0 || zoneY > W) {
            return 0; // kruising onder/boven yard => niet bestaand
        }
        return Math.max(Math.abs(currentX - zoneX), Math.abs(currentY - zoneY));
    }
    */

    /*
    private int timeToRestrictionBorder(Container container, Slot newSlot, String inOrOutZone) {

        int currentX = container.bepaalX();
        int currentY = container.bepaalY(container.getLc());

        int endX = newSlot.getX();
        int endY = newSlot.getY();

        int zoneX = 0;

        if (inOrOutZone.equals("in")) {
            if (currentX < zoneMin) {
                zoneX = zoneMin;
            } else if (currentX > zoneMax) {
                zoneX = zoneMax;
            }
        } else { // out
            if (endX < zoneMin) {
                zoneX = zoneMin;
            } else if (endX > zoneMax) {
                zoneX = zoneMax;
            }
        }

        // snijpunt met restrictie zone
        int zoneY = ((endY-currentY)/(endX-currentX))*(zoneX-currentX) + currentY;
        if (zoneY < 0 || zoneY > W) {
            return 0; // kruising onder/boven yard => niet bestaand
        }
        return Math.max(Math.abs(currentX - zoneX), Math.abs(currentY - zoneY));

    }
    */

    /*
    // kraan uit restriction zone plaatsen
    private int moveCraneOutOfZone(Crane crane) {
        // zoek start locatie voor te kijken naar welke richting crane moet
        int beforeTime = crane.getCraneRoute().getTime();
        if (crane.getXmax() > zoneMax) { // naar rechts
            crane.move(zoneMax+Ls, crane.getY());
        } else if (crane.getXmin() < zoneMin) { // naar links
            crane.move(zoneMin-Ls, crane.getY());
        }

        return crane.getCraneRoute().getTime()-beforeTime; // enkel tijd van het verplaatsen terug geven
    }
    */

    /*
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
     */




    //--------------------------------------------- OLD METHODS: LAB 1 ---------------------------------------------

    /*
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
     */




    //--------------------------------------------- GETTERS AND SETTERS --------------------------------------------

    public static int getL() {
        return L;
    }

    public void setL(int l) {
        L = l;
    }

    public static int getW() {
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
}
