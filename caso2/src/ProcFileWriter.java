import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ProcFileWriter {

     public static void write(String filePath, GeneradorReferencias.Resultado res) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            ProcHeader h = res.header;
            pw.println("TP=" + h.TP);
            pw.println("NF=" + h.NF);
            pw.println("NC=" + h.NC);
            pw.println("NR=" + h.NR);
            pw.println("NP=" + h.NP);

            for (Referencia r : res.refs) {
                pw.println(r.etiqueta + "," + r.pagina + "," + r.offset + "," + r.accion);
            }
        }
    }

}
