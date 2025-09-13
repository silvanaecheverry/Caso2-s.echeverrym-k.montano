import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Caso 2: Memoria Virtual ===");
        System.out.println("Seleccione opción:");
        System.out.println("1. Generar referencias (Opción 1)");
        System.out.println("2. Simulación de ejecución (Opción 2)");
        System.out.print(">> ");

        int opcion = sc.nextInt();
        sc.nextLine(); // limpiar buffer

        if (opcion == 1) {
            // Pedir parámetros que requiere MainOpcion1
            System.out.print("Ruta del archivo de configuración: ");
            String configPath = sc.nextLine();

            System.out.print("Directorio de salida: ");
            String outDir = sc.nextLine();

            // Reusar MainOpcion1 con los argumentos
            MainOpcion1.main(new String[]{configPath, outDir});

        } else if (opcion == 2) {
            // Pedir parámetros que requiere MainOpcion2
            System.out.print("Número total de marcos en RAM: ");
            int totalMarcos = sc.nextInt();

            System.out.print("Número de procesos: ");
            int numProcesos = sc.nextInt();

            // Reusar MainOpcion2 con los argumentos
            MainOpcion2.main(new String[]{String.valueOf(totalMarcos), String.valueOf(numProcesos)});

        } else {
            System.out.println("Opción inválida.");
        }

        sc.close();
    }
}