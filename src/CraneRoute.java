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

    //gebruikt bij verplaatsing zonder opnemen of neerplaatsen container (vb. eindpositie of bij labo 1)
    public void move(int currentX, int currentY, int nextX, int nextY){
        //bewegingen in beide richtingen tegelijk => tijd tot volgende container wordt bepaald door maximale afstand tot die container
        time = time + Math.max(Math.abs(currentX - nextX), Math.abs(currentY - nextY));
        movements.add(time + "," + nextX + "," + nextY + "\n");
    }

    //gebruikt bij verplaatsing naar container (+ opnemen of neerplaatsen)
    public void moveContainer(int currentX, int currentY, int nextX, int nextY, Container container){
        //bewegingen in beide richtingen tegelijk => tijd tot volgende container wordt bepaald door maximale afstand tot die container
        time = time + Math.max(Math.abs(currentX - nextX), Math.abs(currentY - nextY));
        movements.add(time + "," + nextX + "," + nextY + "," + container.getId() + "\n");

        // 2 tijdseenheden nodig voor opnemen of neerplaasten container
        time = time + 2;
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
