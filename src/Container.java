public class Container {
    private int id;
    private int Lc;
    private int Gc;

    public Container(String id, String length, String weight) {
        this.id = Integer.parseInt(id);
        this.Lc = Integer.parseInt(length);
        this.Gc = Integer.parseInt(weight);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
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
