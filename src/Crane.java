public class Crane {

    private int id;
    private int x;
    private int y;
    private CraneRoute craneRoute;

    public Crane(String id, String x, String y) {
        this.id = Integer.parseInt(id);
        this.x = Integer.parseInt(x);
        this.y = Integer.parseInt(y);
        this.craneRoute = new CraneRoute(this.x, this.y);
    }

    //gebruikt bij verplaatsing zonder opnemen of neerplaatsen container (vb. eindpositie of bij labo 1)
    public void move(int nextX, int nextY){
        //verplaatsing opslaan
        craneRoute.move(x, y, nextX, nextY);

        //verplaats kraan naar nieuwe positie
        setX(nextX);
        setY(nextY);
    }

    //gebruikt bij verplaatsing naar container (+ opnemen of neerplaatsen)
    public void moveContainer(int nextX, int nextY, Container container){
        //verplaatsing opslaan
        craneRoute.moveContainer(x, y, nextX, nextY, container);

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
}
