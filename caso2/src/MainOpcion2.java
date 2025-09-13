import java.util.*;

public class MainOpcion2 {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Uso: java MainOpcion2 <numMarcos> <numProcesos>");
            return;
        }

        int totalMarcos = Integer.parseInt(args[0]);
        int numProcesos = Integer.parseInt(args[1]);

        List<Proceso> procesos = new ArrayList<>();

        for (int i = 0; i < numProcesos; i++) {
            String fileName = "caso2/src/Salida/proc" + i + ".txt";
            List<Referencia> refs = ProcFileReader.leerReferencias(fileName);
            int numPaginas = refs.stream().mapToInt(r -> r.pagina).max().orElse(0) + 1;

            Proceso p = new Proceso(i, refs, numPaginas);
            procesos.add(p);
        }

        Simulador sim = new Simulador(numProcesos, totalMarcos);
        sim.run(procesos);
    }
}
