public class ProcHeader {
    
    public final int TP;          // tamaño de página
    public final int NF;          // filas
    public final int NC;          // columnas
    public final long NR;         // total de referencias
    public final int NP;          // # páginas virtuales del proceso

    public ProcHeader(int TP, int NF, int NC, long NR, int NP) {
        this.TP = TP;
        this.NF = NF;
        this.NC = NC;
        this.NR = NR;
        this.NP = NP;
    }
}
