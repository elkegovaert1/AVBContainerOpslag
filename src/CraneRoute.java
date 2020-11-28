import java.util.ArrayList;
import java.util.List;

public class CraneRoute {

    private List<CraneMovement> movements = new ArrayList<>();
    private int time;

    public CraneRoute(int id, int x, int y){
        //startpositie
        this.time = 0;
        movements.add(new CraneMovement(time, id, x, y));
    }

    //gebruikt bij verplaatsing zonder opnemen of neerplaatsen container (vb. eindpositie of bij labo 1)
    public int move(int id, int currentX, int currentY, int nextX, int nextY){
        //bewegingen in beide richtingen tegelijk => tijd tot volgende container wordt bepaald door maximale afstand tot die container
        this.time = this.time + Math.max(Math.abs(currentX - nextX), Math.abs(currentY - nextY));

        movements.add(new CraneMovement(this.time, id, currentX, currentY, nextX, nextY));

        return this.time;
    }

    //gebruikt bij verplaatsing naar container (+ opnemen of neerplaatsen)
    public int moveContainer(int id, int currentX, int currentY, int nextX, int nextY, Container container){
        //bewegingen in beide richtingen tegelijk => tijd tot volgende container wordt bepaald door maximale afstand tot die container
        this.time = this.time + Math.max(Math.abs(currentX - nextX), Math.abs(currentY - nextY));
        movements.add(new CraneMovement(this.time, id, currentX, currentY, nextX, nextY, container.getId()));

        // 2 tijdseenheden nodig voor opnemen of neerplaasten container
        this.time = this.time + 2;
        movements.add(new CraneMovement(this.time, id, currentX, currentY, nextX, nextY));

        return this.time;
    }

    public List<CraneMovement> getMovements() {
        return movements;
    }

    public void setMovements(List<CraneMovement> movements) {
        this.movements = movements;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
