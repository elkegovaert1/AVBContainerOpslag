public class Crane implements Comparable<Crane>{

    private int id;
    private int x;
    private int y;

    //stores the movements of the crane + the time of finishing the last assigned movement
    private CraneRoute craneRoute;

    //safety distance to other cranes, no other cranes can enter zone
    private int xsafe;

    //effective borders for a crane in the yard
    //(when all other cranes to the left or right are the minimal distance from each other)
    private int maxLeft;
    private int maxRight;

    public Crane(String id, String x, String y, String z) {
        this.id = Integer.parseInt(id);
        this.x = Integer.parseInt(x);
        this.y = Integer.parseInt(y);
        this.xsafe = Integer.parseInt(z);
        this.craneRoute = new CraneRoute(this.id, this.x, this.y);
    }

    //------------------------------------------------ CRANE MOVEMENT ----------------------------------------------

    //crane waits in current position until given time
    public void wait(int time){
        //save movement
        craneRoute.setTime(time);
        craneRoute.move(id, x, y, x, y);
    }

    //move the crane to a new location
    public void move(int nextX, int nextY){
        //verplaatsing opslaan
        craneRoute.move(id, x, y, nextX, nextY);

        //verplaats kraan naar nieuwe positie
        setX(nextX);
        setY(nextY);
    }

    //move the crane to a new location and pickup or drop a container
    public void moveContainer(int nextX, int nextY, Container container){
        //verplaatsing opslaan
        craneRoute.moveContainer(id, x, y, nextX, nextY, container);

        //verplaats kraan naar nieuwe positie
        setX(nextX);
        setY(nextY);
    }




    //----------------------------------------------- CRANE LOCATION -----------------------------------------------

    public int getCraneZoneMin(int time) {
        return getCraneLocation(time)-xsafe;
    }

    public int getCraneZoneMax(int time) {
        return getCraneLocation(time)+xsafe;
    }

    //determine the current location of a crane during a movement
    private int getCraneLocation(int time) {

        CraneMovement currentCM = null;
        for (CraneMovement cm: craneRoute.getMovements()) {
            if (cm.getTime() > time) {
                currentCM = cm;
                break;
            }
        }

        if (currentCM == null) {
            return -1;
        }

        int currentX = currentCM.getX();
        int currentY = currentCM.getY();
        int endX = currentCM.getNextX();
        int endY = currentCM.getNextY();

        // als X langere weg dan Y => true
        boolean lengthOrWidth = Math.abs(currentCM.getX() - currentCM.getNextX()) >= Math.abs(currentCM.getY() - currentCM.getNextY());

        // check of we van links naar rechts of averechts bewegen
        boolean linksNaarRechts = currentCM.getX() <= currentCM.getNextX();
        // boven of onder
        boolean onderNaarBoven = currentCM.getY() <= currentCM.getNextY();

        // wat is exacte X of Y-waarde op moment
        int position;
        if (linksNaarRechts && lengthOrWidth) { // als van links naar rechts en we kijken over X
            position = this.getX() + (time - currentCM.getTime());
        } else if (!linksNaarRechts && lengthOrWidth) { // als van rechts naar links en we kijken over X
            position = this.getX() - (time - currentCM.getTime());
        } else if (onderNaarBoven && !lengthOrWidth) { // als onder naar boven en we kijken over Y
            position = this.getY() + (time - currentCM.getTime());
        } else {
            position = this.getY() - (time - currentCM.getTime());
        }

        // minpunt op bepaalde tijd
        if (lengthOrWidth) { // over X
            return position;
        } else { // over Y
            int X = ((endX-currentX)/(endY-currentY))*(position-currentY) + currentX;
            if (X < 0 || X > Yard.getL()) {
                return 0; // kruising onder/boven yard => niet bestaand
            }
            return X;
        }
    }



    //--------------------------------------------- GETTERS AND SETTERS --------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public CraneRoute getCraneRoute() {
        return craneRoute;
    }

    public void setCraneRoute(CraneRoute craneRoute) {
        this.craneRoute = craneRoute;
    }

    public int getXsafe() {
        return xsafe;
    }

    public void setXsafe(int xsafe) {
        this.xsafe = xsafe;
    }

    public int getMaxLeft() {
        return maxLeft;
    }

    public void setMaxLeft(int maxLeft) {
        this.maxLeft = maxLeft;
    }

    public int getMaxRight() {
        return maxRight;
    }

    public void setMaxRight(int maxRight) {
        this.maxRight = maxRight;
    }

    @Override
    public int compareTo(Crane crane) {
        return Integer.compare(this.x, crane.x);
    }
}
