import java.io.File;

public class MainOpcion1 {

    // Uso:
    //   java MainOpcion1 path/al/config.conf path/salida/
    //
    // Ejemplo de config.conf:
    //   TP=128
    //   NPROC=2
    //   TAMS=4x4,8x8
    //
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
            Config cfg = Config.load(configPath);

            for (int i = 0; i < cfg.nproc; i++) {
                int NF = cfg.nfs[i];
                int NC = cfg.ncs[i];
                GeneradorReferencias.Resultado res =
                        GeneradorReferencias.generarParaProceso(cfg.pageSize, NF, NC);

                String procFile = new File(od, "proc" + i + ".txt").getAbsolutePath();
                ProcFileWriter.write(procFile, res);
                System.out.println("Generado: " + procFile);
            }

            System.out.println("Listo. Se generaron " + cfg.nproc + " archivos proc<i>.txt");

        } catch (Exception e) {
            System.err.println("Error ejecutando Opción 1: " + e.getMessage());
            e.printStackTrace();
            System.exit(3);
        }
    }

}
