public class Slot {

    private int id;
    private int x;
    private int y;

    private Container[] containers;
    private int hoogteContainers;

    public Slot(String id, int Hmax) {
        this.id = Integer.parseInt(id);
        this.containers = new Container[Hmax];
        this.hoogteContainers = 0;
    }

    public void addContainer(Container container) {
        if (containers[0] == null) {
            containers[0] = container;
        } else {
            hoogteContainers++;
            containers[hoogteContainers] = container;
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Container[] getContainers() {
        return containers;
    }

    public int getHoogteContainers() {
        return hoogteContainers;
    }

    public void setHoogteContainers(int hoogteContainers) {
        this.hoogteContainers = hoogteContainers;
    }

    public int getX() {
        return x;
    }

    public void setXY(String x, String y) {
        this.x = Integer.parseInt(x);
        this.y = Integer.parseInt(y);
    }

    public int getY() {
        return y;
    }

}
