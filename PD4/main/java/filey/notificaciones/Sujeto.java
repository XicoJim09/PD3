package filey.notificaciones;

public interface Sujeto {
    void registrarObservador(int identificadorEditorial, Observador observador);
    void eliminarObservador(int identificadorEditorial, Observador observador);
    void enviarNotificacion(int identificadorEditorial, String mensajeNotificacion);
}
