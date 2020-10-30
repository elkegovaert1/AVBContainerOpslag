import java.util.ArrayList;
import java.util.List;

public class CraneRoute {

    private List<String> movements = new ArrayList<>();
    private int time;

    public CraneRoute(int x, int y){
        //startpositie
        this.time = 0;
        movements.add(time + "," + x + "," + y + "\n");
    }

    public void move(int currentX, int currentY, int nextX, int nextY){
        //bewegingen in beide richtingen tegelijk => tijd tot volgende container wordt bepaald door maximale afstand tot die container
        time = time + Math.max(Math.abs(currentX - nextX), Math.abs(currentY - nextY));
        movements.add(time + "," + nextX + "," + nextY + "\n");
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<String> getMovements() {
        return movements;
    }

    public void setMovements(List<String> movements) {
        this.movements = movements;
    }
}
