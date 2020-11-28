public class Crane {

    private int id;
    private int x;
    private int y;
    private int xmin;
    private int xmax;
    private int time;
    private CraneRoute craneRoute;

    public Crane(String id, String x, String y, String z, String a) {
        this.id = Integer.parseInt(id);
        this.x = Integer.parseInt(x);
        this.y = Integer.parseInt(y);
        this.xmin = Integer.parseInt(z);
        this.xmax = Integer.parseInt(a);
        this.craneRoute = new CraneRoute(this.id, this.x, this.y);
        this.time = 0;
    }

    //gebruikt bij verplaatsing zonder opnemen of neerplaatsen container (vb. eindpositie of bij labo 1)
    public void move(int nextX, int nextY){
        //verplaatsing opslaan
        this.time = time + craneRoute.move(id, x, y, nextX, nextY);

        //verplaats kraan naar nieuwe positie
        setX(nextX);
        setY(nextY);
    }

    //gebruikt bij verplaatsing naar container (+ opnemen of neerplaatsen)
    public void moveContainer(int nextX, int nextY, Container container){
        //verplaatsing opslaan
        this.time = time + craneRoute.moveContainer(id, x, y, nextX, nextY, container);

        //verplaats kraan naar nieuwe positie
        setX(nextX);
        setY(nextY);
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        this.craneRoute.setTime(time);
    }
}
