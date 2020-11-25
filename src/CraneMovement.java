public class CraneMovement {
    private int time;
    private int craneId;
    private int containerId;
    private int x;
    private int y;
    private int nextX;
    private int nextY;
    private String type;

    //startpositie
    public CraneMovement(int time, int craneId, int x, int y) {
        this.time = time;
        this.craneId = craneId;
        this.x = x;
        this.y = y;
        this.type = "start";
    }

    //verplaatsing zonder opnemen
    public CraneMovement(int time, int craneId, int x, int y, int nextX, int nextY) {
        this.time = time;
        this.craneId = craneId;
        this.x = x;
        this.y = y;
        this.nextX = nextX;
        this.nextY = nextY;
        this.type = "move";
    }

    //verplaatsing met container
    public CraneMovement(int time, int craneId, int x, int y, int nextX, int nextY, int containerId) {
        this.time = time;
        this.craneId = craneId;
        this.x = x;
        this.y = y;
        this.nextX = nextX;
        this.nextY = nextY;
        this.containerId = containerId;
        this.type = "container";
    }

    @Override
    public String toString() {

        switch (type){
            case "start" : return time + "," + craneId + "," + x + "," + y + "\n";
            case "move" : return time + "," + craneId + "," + nextX + "," + nextY + "\n";
            case "container" : return time + "," + craneId + "," + nextX + "," + nextY + "," + containerId + "\n";
        }
        return "no type";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getContainerId() {
        return containerId;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCraneId() {
        return craneId;
    }

    public void setCraneId(int craneId) {
        this.craneId = craneId;
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

    public int getNextX() {
        return nextX;
    }

    public void setNextX(int nextX) {
        this.nextX = nextX;
    }

    public int getNextY() {
        return nextY;
    }

    public void setNextY(int nextY) {
        this.nextY = nextY;
    }
}
