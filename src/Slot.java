import java.util.ArrayList;
import java.util.List;

public class Slot {

    private int id;
    private int x;
    private int y;

    private List<Container> containers;

    public Slot(String id) {
        this.id = Integer.parseInt(id);
        this.containers = new ArrayList<>();
    }

    public void addContainer(Container container) {
        containers.add(container);
    }

    public Container removeContainer(){
        return containers.remove(containers.size()-1);
    }

    public boolean isSorted(){
        if(containers.size() >= 2){
            for(int i = 1; i<containers.size(); i++){
                if (containers.get(i-1).getGc() > containers.get(i).getGc())
                    return false;
            }
        }
        return true;
    }

    public boolean isSorted(Container container){
        containers.add(container);
        if(containers.size() >= 2){
            for(int i = 1; i<containers.size(); i++){
                if (containers.get(i-1).getGc() > containers.get(i).getGc())
                    containers.remove(container);
                    return false;
            }
        }
        containers.remove(container);
        return true;
    }

    //geen gewicht 1 bovenaan
    public boolean checkWeight(){
        return containers.get(containers.size()-1).getGc() > 1;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
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
