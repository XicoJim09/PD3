package filey;

import filey.datos.Configuracion;
import filey.datos.Editorial;
import filey.datos.Reserva;
import filey.datos.Stand;
import filey.controlador.ControladorFiley;
import filey.persistencia.ConexionSQLite;
import filey.persistencia.InicializadorSQLite;
import filey.negocio.SistemaFiley;
import filey.notificaciones.Notificador;
import filey.vista.VistaConsola;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

public final class Main {
    private Main() { }

    public static void main(String[] argumentos) throws SQLException, IOException {
        String url = "jdbc:sqlite:feria.db";
    System.out.println("Intentando conectar a SQLite...");

    try (Connection conn = DriverManager.getConnection(url)) {
        if (conn != null) {
            System.out.println("¡Conexión a SQLite exitosa!");
        }
    } catch (SQLException e) {
        System.out.println("Error de conexión: " + e.getMessage());
    }
    
    // Aquí abajo debe continuar tu código anterior...
        mostrarLecturasBaseDatos();

        // Fechas de ejemplo relativas a hoy; se pueden cambiar sin modificar los servicios.
        LocalDate fechaActual = LocalDate.now();
        Configuracion parametrosSistema = new Configuracion(fechaActual.plusDays(4), fechaActual.plusDays(10),
                fechaActual.plusDays(6), 3, 2);
        SistemaFiley sistemaFiley = new SistemaFiley(parametrosSistema, new Notificador());
        Stand standPequeno = new Stand(1, new BigDecimal("2"), new BigDecimal("5"), new BigDecimal("100"));
        Stand standMediano = new Stand(2, new BigDecimal("3"), new BigDecimal("4"), new BigDecimal("100"));
        sistemaFiley.agregarStand(standPequeno);
        sistemaFiley.agregarStand(standMediano);
        sistemaFiley.agregarEditorial(new Editorial(1, "Lecturas del Mayab"));
        sistemaFiley.agregarEditorial(new Editorial(2, "Letras del Sur"));
        System.out.println("Stands disponibles: " + sistemaFiley.listarStandsDisponibles().stream()
                .map(Stand::obtenerIdentificador).toList());
        Reserva reservaInicial = sistemaFiley.reservarStand(1, 1, fechaActual);
        System.out.println("Reserva " + reservaInicial.obtenerIdentificador() + " de "
                + reservaInicial.obtenerEditorial().obtenerNombre()
                + "; total: $" + reservaInicial.obtenerImporteTotal()
                + "; vence: " + reservaInicial.obtenerFechaVencimiento());
        sistemaFiley.agregarPagoReserva(reservaInicial.obtenerIdentificador(),
                new BigDecimal("100"), fechaActual);
        imprimirResumenReserva(reservaInicial);
        System.out.println("--- Comprobación de avisos antes del segundo pago ---");
        sistemaFiley.revisarFechasLimite(fechaActual.plusDays(2));
        sistemaFiley.agregarPagoReserva(reservaInicial.obtenerIdentificador(),
                new BigDecimal("50"), fechaActual.plusDays(2));
        imprimirResumenReserva(reservaInicial);
        sistemaFiley.bloquearStand(2);
        System.out.println("Stand 2: " + standMediano.obtenerEstadoActual());
        sistemaFiley.desbloquearStand(2);
        System.out.println("Stand 2: " + standMediano.obtenerEstadoActual());
        Reserva reservaSinPagos = sistemaFiley.reservarStand(2, 2, fechaActual.plusDays(2));
        sistemaFiley.revisarFechasLimite(fechaActual.plusDays(4));
        sistemaFiley.revisarFechasLimite(fechaActual.plusDays(6));
        System.out.println("Reserva " + reservaSinPagos.obtenerIdentificador() + " vencida: "
                + reservaSinPagos.estaVencida() + "; stand 2: " + standMediano.obtenerEstadoActual());
        System.out.println("--- Aviso de liquidación; la segunda consulta no duplica avisos ---");
        sistemaFiley.revisarFechasLimite(fechaActual.plusDays(8));
        sistemaFiley.revisarFechasLimite(fechaActual.plusDays(8));
        sistemaFiley.agregarPagoReserva(reservaInicial.obtenerIdentificador(),
                new BigDecimal("850"), fechaActual.plusDays(10));
        imprimirResumenReserva(reservaInicial);
    }

    private static void mostrarLecturasBaseDatos() throws SQLException, IOException {
        try (Connection conexion = ConexionSQLite.abrirConexion()) {
            InicializadorSQLite.inicializar(conexion);
            ControladorFiley controlador = new ControladorFiley(conexion);
            VistaConsola vista = new VistaConsola();
            vista.mostrarConexion(conexion.getMetaData().getURL());
            vista.mostrarStands(controlador.listarStands());
            vista.mostrarEditoriales(controlador.listarEditoriales());
            vista.mostrarReservas(controlador.listarReservas());
        }
    }

    private static void imprimirResumenReserva(Reserva reservaConsultada) {
        System.out.println("Stand " + reservaConsultada.obtenerStand().obtenerIdentificador()
                + ": " + reservaConsultada.obtenerStand().obtenerEstadoActual()
                + "; abonado: $" + reservaConsultada.calcularTotalAbonado()
                + "; pendiente: $" + reservaConsultada.calcularSaldoPendiente()
                + "; porcentaje: " + reservaConsultada.calcularPorcentajePagado() + "%");
    }
}
