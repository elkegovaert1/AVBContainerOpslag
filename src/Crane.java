import java.util.List;

public class Crane {

    private int id;
    private int x;
    private int y;
    private int xmin;
    private int xmax;
    private CraneRoute craneRoute;

    public Crane(String id, String x, String y, String z, String a) {
        this.id = Integer.parseInt(id);
        this.x = Integer.parseInt(x);
        this.y = Integer.parseInt(y);
        this.xmin = Integer.parseInt(z);
        this.xmax = Integer.parseInt(a);
        this.craneRoute = new CraneRoute(this.id, this.x, this.y);
    }

    //gebruikt bij verplaatsing zonder opnemen of neerplaatsen container (vb. eindpositie of bij labo 1)
    public void move(int nextX, int nextY){
        //verplaatsing opslaan
        craneRoute.move(id, x, y, nextX, nextY);

        //verplaats kraan naar nieuwe positie
        setX(nextX);
        setY(nextY);
    }

    //gebruikt bij verplaatsing naar container (+ opnemen of neerplaatsen)
    public void moveContainer(int nextX, int nextY, Container container){
        //verplaatsing opslaan
        craneRoute.moveContainer(id, x, y, nextX, nextY, container);

        //verplaats kraan naar nieuwe positie
        setX(nextX);
        setY(nextY);
    }

    // -1 als nog niet zo ver
    private int getZoneMinOnCertainTime(int time) {

        int currentX = this.getX();
        int currentY = this.getY();
        int endX = this.getX();
        int endY = this.getY();

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

    public int getXmin() {
        return xmin;
    }

    public void setXmin(int xmin) {
        this.xmin = xmin;
    }

    public int getXmax() {
        return xmax;
    }

    public void setXmax(int xmax) {
        this.xmax = xmax;
    }

}
