import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Config {
    public final int pageSize;  // TP
    public final int nproc;     // NPROC
    public final int[] nfs;     // filas por proceso
    public final int[] ncs;     // cols por proceso

    private static final boolean DEBUG = true; // pon en false para silenciar

    public Config(int pageSize, int nproc, int[] nfs, int[] ncs) {
        this.pageSize = pageSize;
        this.nproc = nproc;
        this.nfs = nfs;
        this.ncs = ncs;
    }

    public static Config load(String path) throws IOException {
        int TP = -1;
        int NPROC = -1;
        String tams = null;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = stripBOM(line).strip();  // quita BOM y espacios laterales
                if (line.isEmpty()) continue;

                if (line.startsWith("TP=")) {
                    String val = line.substring(3).trim();
                    if (DEBUG) System.out.println("DEBUG TP=«" + val + "»");
                
                
                    TP = Integer.parseInt(val);
                } else if (line.startsWith("NPROC=")) {
                    String val = line.substring(7).trim();
                    if (DEBUG) System.out.println("DEBUG NPROC=«" + val + "»");
                    NPROC = Integer.parseInt(val);
                } else if (line.startsWith("TAMS=")) {
                    // normalizamos: x en minúscula, cambiamos × y ✕ por x, quitamos espacios
                    tams = normalizeTams(line.substring(5));
                    if (DEBUG) System.out.println("DEBUG TAMS(normalized)=«" + tams + "»");
                }
            }
        }

        if (TP <= 0 || NPROC <= 0 || tams == null || tams.isEmpty()) {
            throw new IllegalArgumentException("Config inválida: verifique TP, NPROC y TAMS.");
        }

        String[] parts = tams.split(",");
        if (parts.length != NPROC) {
            throw new IllegalArgumentException("El número de tamaños en TAMS (" + parts.length
                    + ") no coincide con NPROC (" + NPROC + ").");
        }

        int[] nfs = new int[NPROC];
        int[] ncs = new int[NPROC];

        for (int i = 0; i < NPROC; i++) {
            String p = parts[i]; // ya viene normalizado y sin espacios
            String[] xy = p.split("x");
            if (DEBUG) {
                System.out.println("DEBUG TAMS["+i+"]: p=«" + p + "» xy=" + Arrays.toString(xy));
            }
            if (xy.length != 2 || xy[0].isEmpty() || xy[1].isEmpty()) {
                throw new IllegalArgumentException("Tamaño inválido en TAMS para el proceso " + i + ": «" + p + "»");
            }
            nfs[i] = Integer.parseInt(xy[0]);
            ncs[i] = Integer.parseInt(xy[1]);
            if (nfs[i] <= 0 || ncs[i] <= 0) {
                throw new IllegalArgumentException("Tamaño inválido (<=0) en proceso " + i + ": «" + p + "»");
            }
        }

        return new Config(TP, NPROC, nfs, ncs);
    }

    private static String stripBOM(String s) {
        if (!s.isEmpty() && s.charAt(0) == '\uFEFF') {
            return s.substring(1);
        }
        return s;
    }

    private static String normalizeTams(String raw) {
        // Normaliza: quita espacios (incluyendo NBSP), convierte "×" (U+00D7) y "✕" (U+2715) a 'x', y 'X' a 'x'
        String s = stripBOM(raw)
                .replace('\u00A0', ' ')  // NBSP -> espacio
                .trim()
                .toLowerCase()
                .replace('×', 'x')       // multiplicación
                .replace('✕', 'x')       // otra variante de multiplicación
                .replace('x', 'x')       // asegura minúscula
                .replace(" ", "");       // quita espacios en todo

        // ejemplo: "4x4, 8×8" -> "4x4,8x8"
        return s;
    }
}