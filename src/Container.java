import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Container {

    private int id;
    private int Lc; //lengte container (20,40,60)
    private int Gc; //gewicht container (1,2,3)

    private List<Slot> slots;

    public Container(String id, String length, String weight) {
        this.id = Integer.parseInt(id);
        this.Lc = Integer.parseInt(length);
        this.Gc = Integer.parseInt(weight);

        slots = new ArrayList<>();
    }

    public void addSlot(Slot slot){
        slots.add(slot);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public int bepaalX(){
        //sorteer slots op x waarde voor zekerheid (klopt normaal al vanuit inlezen )
        Collections.sort(slots);

        return slots.get(slots.size()/2).getX();
    }

    public int bepaalY(int width){
        return slots.get(0).getY()+width/2;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLc() {
        return Lc;
    }

    public void setLc(int lc) {
        this.Lc = lc;
    }

    public int getGc() {
        return Gc;
    }

    public void setGc(int gc) {
        this.Gc = gc;
    }

}
