import java.io.File;

public class MainOpcion1 {

    /**
     * Uso:
     *   java -cp out MainOpcion1 <ruta_config> <directorio_salida>
     *
     * Ejemplo:
     *   java -cp out MainOpcion1 caso2/src/config.conf caso2/src/salida
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java MainOpcion1 <ruta_config> <directorio_salida>");
            System.exit(1);
        }

        String configPath = args[0];
        String outDir = args[1];
        File od = new File(outDir);
        if (!od.exists() && !od.mkdirs()) {
            System.err.println("No se pudo crear el directorio de salida: " + outDir);
            System.exit(2);
        }

        try {
            // Cargar configuración
            Config cfg = Config.load(configPath);

            // Para cada proceso generar archivo proc<i>.txt
            for (int i = 0; i < cfg.nproc; i++) {
                
                int NF = cfg.nfs[i];
                int NC = cfg.ncs[i];

                // Generar referencias para el proceso i
                GeneradorReferencias.Resultado res =
                        GeneradorReferencias.generarParaProceso(cfg.pageSize, NF, NC);

                // Construir ruta de salida para proc<i>.txt
                String procFile = new File(od, "proc" + i + ".txt").getAbsolutePath();

                // Escribir archivo
                ProcFileWriter.write(procFile, res);

                System.out.println("Generado: " + procFile +
                                   " (NF=" + NF + ", NC=" + NC + ")");
            }

            System.out.println("Listo. Se generaron " + cfg.nproc + " archivos proc<i>.txt");

        } catch (Exception e) {
            System.err.println("Error ejecutando Opción 1: " + e.getMessage());
            e.printStackTrace();
            System.exit(3);
        }
    }

}
