public class Crane {

    private int id;
    private int x;
    private int y;

    public Crane(String id, String x, String y) {
        this.id = Integer.parseInt(id);
        this.x = Integer.parseInt(x);
        this.y = Integer.parseInt(y);
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
}
