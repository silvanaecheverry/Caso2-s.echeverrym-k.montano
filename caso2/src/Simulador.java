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
                // liberar marcos
                for (Marco m : marcosPorProceso.get(p.getId())) {
                    m.procesoId = -1;
                    m.paginaVirtual = -1;
                }
                continue;
            }

            Referencia ref = p.getReferenciaActual();
            PageEntry entrada = p.getEntrada(ref.pagina);

            // Caso: la referencia fue un fallo en la vuelta anterior -avanzar sin contar hit
            if (p.debeReintentar()) {
                p.limpiarReintento();
                p.avanzar();
            }
            else if (entrada.cargada) {
                // HIT
                p.hits++;
                entrada.lastAccess = ++clock;
                p.avanzar();
            } else {
                // FALLO
                p.fallos++;
                p.swaps++;

                List<Marco> marcosAsignados = marcosPorProceso.get(p.getId());
                Marco libre = buscarLibre(marcosAsignados);

                if (libre != null) {
                    libre.procesoId = p.getId();
                    libre.paginaVirtual = ref.pagina;
                    entrada.cargada = true;
                    entrada.marco = libre.id;
                    entrada.lastAccess = ++clock;
                } else {
                    Marco victima = elegirVictimaLRU(p, marcosAsignados);
                    PageEntry entradaVieja = p.getEntrada(victima.paginaVirtual);
                    entradaVieja.cargada = false;

                    victima.paginaVirtual = ref.pagina;
                    entrada.cargada = true;
                    entrada.marco = victima.id;
                    entrada.lastAccess = ++clock;
                }

                // marcar la referencia para reintento
                p.marcarReintento();
            }

            envejecerPaginas(p);

            if (p.tieneMasReferencias()) {
                cola.add(p);
            }
        }

        // Al final: imprimir estadísticas
        System.out.println("\n=== Estadísticas finales ===");
        for (Proceso p : procesos) {
            System.out.println("\nProceso " + p.getId());
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

    private Marco buscarLibre(List<Marco> marcosAsignados) {
        for (Marco m : marcosAsignados) {
            if (m.procesoId == -1) return m;
        }
        return null;
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

    // Envejecimiento básico
    private void envejecerPaginas(Proceso p) {
        for (Marco m : marcosPorProceso.get(p.getId())) {
            if (m.procesoId != -1) {
                PageEntry entrada = p.getEntrada(m.paginaVirtual);
                entrada.lastAccess--; 
            }
        }
    }
}
