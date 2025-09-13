public class PageEntry {
    boolean cargada;
    int marco;
    long lastAccess; // para LRU

    public PageEntry() {
        this.cargada = false;
        this.marco = -1;
        this.lastAccess = -1;
    }
}
