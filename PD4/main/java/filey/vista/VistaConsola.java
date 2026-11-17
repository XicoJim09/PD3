package filey.vista;

import filey.modelo.EditorialRegistro;
import filey.modelo.ReservaRegistro;
import filey.modelo.StandRegistro;
import java.util.List;

public final class VistaConsola {
    public void mostrarConexion(String urlConexion) {
        System.out.println("Conexión JDBC establecida: " + urlConexion);
    }

    public void mostrarStands(List<StandRegistro> stands) {
        System.out.println("\n--- Tabla stands ---");
        if (stands.isEmpty()) {
            System.out.println("(sin registros)");
            return;
        }
        for (StandRegistro stand : stands) {
            System.out.printf("ID: %d | dimensiones: %s x %s m | precio/m²: $%s | estado: %s%n",
                    stand.identificadorStand(), stand.anchoEnMetros(), stand.largoEnMetros(),
                    stand.precioPorMetroCuadrado(), stand.estado());
        }
    }

    public void mostrarEditoriales(List<EditorialRegistro> editoriales) {
        System.out.println("\n--- Tabla editoriales ---");
        if (editoriales.isEmpty()) {
            System.out.println("(sin registros)");
            return;
        }
        for (EditorialRegistro editorial : editoriales) {
            System.out.printf("ID: %d | nombre: %s%n",
                    editorial.identificadorEditorial(), editorial.nombreEditorial());
        }
    }

    public void mostrarReservas(List<ReservaRegistro> reservas) {
        System.out.println("\n--- Tabla reservas ---");
        if (reservas.isEmpty()) {
            System.out.println("(sin registros)");
            return;
        }
        for (ReservaRegistro reserva : reservas) {
            System.out.printf("ID: %d | editorial: %s (%d) | stand: %d | total: $%s"
                            + " | creación: %s | vence: %s | liquidación: %s | estado: %s%n",
                    reserva.identificadorReserva(), reserva.nombreEditorial(),
                    reserva.identificadorEditorial(), reserva.identificadorStand(),
                    reserva.precioTotal(), reserva.fechaCreacion(), reserva.fechaVencimiento(),
                    reserva.fechaLimiteLiquidacion(), reserva.estado());
        }
    }
}
