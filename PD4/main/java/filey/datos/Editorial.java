package filey.datos;

import filey.notificaciones.Observador;

public final class Editorial implements Observador {
    private final int identificadorEditorial;
    private final String nombreEditorial;

    public Editorial(int identificadorEditorial, String nombreEditorial) {
        if (identificadorEditorial <= 0 || nombreEditorial == null || nombreEditorial.isBlank()) {
            throw new IllegalArgumentException("La editorial requiere ID positivo y nombre.");
        }
        this.identificadorEditorial = identificadorEditorial;
        this.nombreEditorial = nombreEditorial.strip();
    }

    public int obtenerIdentificador() { return identificadorEditorial; }
    public String obtenerNombre() { return nombreEditorial; }

    @Override
    public void recibirNotificacion(String mensajeNotificacion) {
        System.out.println("[Aviso a " + nombreEditorial + "] " + mensajeNotificacion);
    }
}
