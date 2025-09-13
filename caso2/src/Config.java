import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Config {

    public final int pageSize;  // TP
    public final int nproc;     // NPROC
    public final int[] nfs;     // filas por proceso
    public final int[] ncs;     // cols por proceso

    private Config(int pageSize, int nproc, int[] nfs, int[] ncs) {
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
                line = line.strip();
                if (line.isEmpty()) continue;
                if (line.startsWith("TP=")) {
                    TP = Integer.parseInt(line.substring(3).trim());
                } else if (line.startsWith("NPROC=")) {
                    NPROC = Integer.parseInt(line.substring(7).trim());
                } else if (line.startsWith("TAMS=")) {
                    tams = line.substring(5).trim();
                }
            }
        }

        if (TP <= 0 || NPROC <= 0 || tams == null || tams.isEmpty()) {
            throw new IllegalArgumentException("Config inválida: verifique TP, NPROC y TAMS.");
        }

        // Parseo TAMS: ejemplo "4x4,8x8"
        String[] parts = tams.split(",");
        if (parts.length != NPROC) {
            throw new IllegalArgumentException("El número de tamaños en TAMS no coincide con NPROC.");
        }
        int[] nfs = new int[NPROC];
        int[] ncs = new int[NPROC];
        for (int i = 0; i < NPROC; i++) {
            String p = parts[i].trim().toLowerCase();
            String[] xy = p.split("x");
            if (xy.length != 2) {
                throw new IllegalArgumentException("Formato TAMS inválido en: " + p + " (use NxM, p.ej. 4x4)");
            }
            nfs[i] = Integer.parseInt(xy[0].trim());
            ncs[i] = Integer.parseInt(xy[1].trim());
            if (nfs[i] <= 0 || ncs[i] <= 0) {
                throw new IllegalArgumentException("Tamaño de matriz inválido para proceso " + i);
            }
        }

        return new Config(TP, NPROC, nfs, ncs);
    }
    
}
