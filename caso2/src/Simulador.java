import java.util.*;

public class Simulador {
    private int numProcesos;
    private int totalMarcos;
    private List<Marco> marcos;
    private long clock = 0; // para timestamps
    private Map<Integer, List<Marco>> marcosPorProceso = new HashMap<>();

    public Simulador(int numProcesos, int totalMarcos) {
        this.numProcesos = numProcesos;
        this.totalMarcos = totalMarcos;
        this.marcos = new ArrayList<>();
        for (int i = 0; i < totalMarcos; i++) {
            marcos.add(new Marco(i));
        }
    }

    public void run(List<Proceso> procesos) {
        // Asignación inicial de marcos (equitativa)
        int marcosPorProc = totalMarcos / numProcesos;
        for (int i = 0; i < procesos.size(); i++) {
            marcosPorProceso.put(i, new ArrayList<>());
            for (int j = 0; j < marcosPorProc; j++) {
                Marco m = marcos.remove(0);
                marcosPorProceso.get(i).add(m);
            }
        }

        Queue<Proceso> cola = new LinkedList<>(procesos);

        while (!cola.isEmpty()) {
            Proceso p = cola.poll();
            if (!p.tieneMasReferencias()) {
                continue; // este proceso terminó
            }

            Referencia ref = p.getReferenciaActual();
            PageEntry entrada = p.getEntrada(ref.pagina);

            if (entrada.cargada) {
                // HIT
                p.hits++;
                entrada.lastAccess = ++clock;
                p.avanzar();
            } else {
                // FALLO
                p.fallos++;

                List<Marco> marcosAsignados = marcosPorProceso.get(p.getId());
                // ¿queda marco libre?
                Marco libre = null;
                for (Marco m : marcosAsignados) {
                    if (m.procesoId == -1) {
                        libre = m;
                        break;
                    }
                }

                if (libre != null) {
                    // Fallo sin reemplazo
                    libre.procesoId = p.getId();
                    libre.paginaVirtual = ref.pagina;
                    entrada.cargada = true;
                    entrada.marco = libre.id;
                    entrada.lastAccess = ++clock;
                    p.swaps += 1;
                    // ⚠️ NO avanzar aún: reinsertar para procesar la misma referencia en el próximo turno
                    cola.add(p);
                    continue;
                } else {
                    // Fallo con reemplazo (LRU)
                    Marco victima = elegirVictimaLRU(p, marcosAsignados);
                    // Liberar entrada vieja
                    PageEntry entradaVieja = p.getEntrada(victima.paginaVirtual);
                    entradaVieja.cargada = false;
                    // Reemplazar
                    victima.paginaVirtual = ref.pagina;
                    entrada.cargada = true;
                    entrada.marco = victima.id;
                    entrada.lastAccess = ++clock;
                    p.swaps += 2;
                    // Reinsertar proceso sin avanzar
                    cola.add(p);
                    continue;
                }
            }

            // Si aún tiene referencias, reinsertar en cola
            if (p.tieneMasReferencias()) {
                cola.add(p);
            }
        }

        // Al final: imprimir estadísticas
        for (Proceso p : procesos) {
            System.out.println("Proceso " + p.getId());
            System.out.println(" - Num referencias: " + p.totalReferencias());
            System.out.println(" - Fallas: " + p.fallos);
            System.out.println(" - Hits: " + p.hits);
            System.out.println(" - SWAP: " + p.swaps);
            double tasaFalla = (double) p.fallos / p.totalReferencias();
            double tasaExito = (double) p.hits / p.totalReferencias();
            System.out.println(" - Tasa fallas: " + String.format("%.4f", tasaFalla));
            System.out.println(" - Tasa éxito: " + String.format("%.4f", tasaExito));
        }
    }

    private Marco elegirVictimaLRU(Proceso p, List<Marco> marcosAsignados) {
        Marco victima = null;
        long min = Long.MAX_VALUE;
        for (Marco m : marcosAsignados) {
            PageEntry entrada = p.getEntrada(m.paginaVirtual);
            if (entrada.lastAccess < min) {
                min = entrada.lastAccess;
                victima = m;
            }
        }
        return victima;
    }
}
