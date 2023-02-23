public class Identificador {
    private int id;

    public Identificador() {
        this.id = 0;
    }

    public synchronized int getId() {
        id++;
        return this.id;
    }

}
