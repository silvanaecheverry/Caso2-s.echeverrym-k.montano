import java.io.*;
import java.util.*;

public class ProcFileReader {

    /**
     * Lee un archivo proc<i>.txt y retorna la lista de referencias.
     * @param filePath ruta al archivo proc<i>.txt
     */
    public static List<Referencia> leerReferencias(String filePath) throws IOException {
        List<Referencia> referencias = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // --- Saltar las 5 líneas de encabezado (TP, NF, NC, NR, NP) ---
            for (int i = 0; i < 5; i++) {
                br.readLine();
            }

            // --- Procesar las referencias ---
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Ejemplo de línea: M1:[0-0],0,64,r
                String[] partes = line.split(",");

                String etiqueta = partes[0].trim();            // "M1:[0-0]"
                int pagina = Integer.parseInt(partes[1].trim()); // ej. 0
                int offset = Integer.parseInt(partes[2].trim()); // ej. 64
                char accion = partes[3].trim().charAt(0);        // 'r' o 'w'

                referencias.add(new Referencia(etiqueta, pagina, offset, accion));
            }
        }

        return referencias;
    }
}
