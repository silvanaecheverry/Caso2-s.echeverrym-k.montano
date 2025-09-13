import java.util.*;

public class Proceso {
    private int id;
    private List<Referencia> referencias;
    private int puntero; // índice de la siguiente referencia
    private PageEntry[] tablaPaginas;

    // Estadísticas
    public int hits = 0;
    public int fallos = 0;
    public int swaps = 0;

    public Proceso(int id, List<Referencia> refs, int numPaginas) {
        this.id = id;
        this.referencias = refs;
        this.puntero = 0;
        this.tablaPaginas = new PageEntry[numPaginas];
        for (int i = 0; i < numPaginas; i++) {
            tablaPaginas[i] = new PageEntry();
        }
    }

    public int getId() { return id; }

    public boolean tieneMasReferencias() {
        return puntero < referencias.size();
    }

    public Referencia getReferenciaActual() {
        return referencias.get(puntero);
    }

    public void avanzar() {
        puntero++;
    }

    public PageEntry getEntrada(int paginaVirtual) {
        return tablaPaginas[paginaVirtual];
    }

    public int totalReferencias() {
        return referencias.size();
    }
}
