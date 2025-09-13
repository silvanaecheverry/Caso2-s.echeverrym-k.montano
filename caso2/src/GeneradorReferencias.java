import java.util.ArrayList;
import java.util.List;

public class GeneradorReferencias {

    public static class Resultado {
        public final ProcHeader header;
        public final List<Referencia> refs;
        public Resultado(ProcHeader h, List<Referencia> r) { this.header = h; this.refs = r; }
    }

    public static Resultado generarParaProceso(int TP, int NF, int NC) {
        final int BYTES_INT = 4;

        long sizeM1 = (long) NF * NC * BYTES_INT;
        long sizeM2 = sizeM1;
        long sizeM3 = sizeM1;

        long baseM1 = 0L;
        long baseM2 = baseM1 + sizeM1;
        long baseM3 = baseM2 + sizeM2;

        long totalBytes = baseM3 + sizeM3;
        int NP = (int) Math.ceil(totalBytes / (double) TP);

        long NR = (long) NF * NC * 3L; // M1 read + M2 read + M3 write por celda

        List<Referencia> out = new ArrayList<>((int)Math.min(Integer.MAX_VALUE, NR));

        for (int i = 0; i < NF; i++) {
            for (int j = 0; j < NC; j++) {
                long elemIndex = (long) i * NC + j;
                long addrM1 = baseM1 + elemIndex * BYTES_INT;
                long addrM2 = baseM2 + elemIndex * BYTES_INT;
                long addrM3 = baseM3 + elemIndex * BYTES_INT;

                // M1 lectura
                out.add(cref("M1:[" + i + "-" + j + "]", addrM1, TP, 'r'));
                // M2 lectura
                out.add(cref("M2:[" + i + "-" + j + "]", addrM2, TP, 'r'));
                // M3 escritura
                out.add(cref("M3:[" + i + "-" + j + "]", addrM3, TP, 'w'));
            }
        }

        ProcHeader header = new ProcHeader(TP, NF, NC, NR, NP);
        return new Resultado(header, out);
    }

    private static Referencia cref(String etiqueta, long addr, int TP, char accion) {
        int pagina = (int) (addr / TP);
        int offset = (int) (addr % TP);
        return new Referencia(etiqueta, pagina, offset, accion);
        // Formato de línea final: <etiqueta>,<pagina>,<offset>,<accion>
        // Ej: "M1:[0-0],0,0,r"
    }

}
