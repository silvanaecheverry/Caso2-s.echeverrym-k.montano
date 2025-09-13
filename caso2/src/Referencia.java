public class Referencia {

    public final String etiqueta; // ej. "M1:[i-j]" o "M2:[i-j]" o "M3:[i-j]"
    public final int pagina;      // número de página virtual
    public final int offset;      // desplazamiento dentro de la página
    public final char accion;     // 'r' o 'w' en minúscula (como en el anexo)

    public Referencia(String etiqueta, int pagina, int offset, char accion) {
        this.etiqueta = etiqueta;
        this.pagina = pagina;
        this.offset = offset;
        this.accion = accion;
    }

}
