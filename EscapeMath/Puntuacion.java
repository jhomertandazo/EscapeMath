public class Puntuacion {

    public final String usuario;
    public final int puntaje;
    public final int retosCompletados;
    public final String resultado;
    public final String fecha;

    public Puntuacion(String usuario, int puntaje, int retosCompletados, String resultado, String fecha) {
        this.usuario = usuario;
        this.puntaje = puntaje;
        this.retosCompletados = retosCompletados;
        this.resultado = resultado;
        this.fecha = fecha;
    }
}