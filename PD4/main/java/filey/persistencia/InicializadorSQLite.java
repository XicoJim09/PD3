package filey.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Objects;

public final class InicializadorSQLite {
    private InicializadorSQLite() { }

    public static void inicializar(Connection conexion) throws SQLException {
        Objects.requireNonNull(conexion);
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");
            sentencia.execute("""
                    CREATE TABLE IF NOT EXISTS stands (
                        id INTEGER PRIMARY KEY,
                        ancho TEXT NOT NULL,
                        largo TEXT NOT NULL,
                        precio_por_metro_cuadrado TEXT NOT NULL,
                        estado TEXT NOT NULL
                    )
                    """);
            sentencia.execute("""
                    CREATE TABLE IF NOT EXISTS editoriales (
                        id INTEGER PRIMARY KEY,
                        nombre TEXT NOT NULL
                    )
                    """);
            sentencia.execute("""
                    CREATE TABLE IF NOT EXISTS reservas (
                        id INTEGER PRIMARY KEY,
                        editorial_id INTEGER NOT NULL,
                        stand_id INTEGER NOT NULL,
                        fecha_creacion TEXT NOT NULL,
                        fecha_vencimiento TEXT NOT NULL,
                        fecha_limite_liquidacion TEXT NOT NULL,
                        precio_total TEXT NOT NULL,
                        estado TEXT NOT NULL,
                        FOREIGN KEY (editorial_id) REFERENCES editoriales(id),
                        FOREIGN KEY (stand_id) REFERENCES stands(id)
                    )
                    """);
        }
        insertarDatosDemostracion(conexion);
    }

    private static void insertarDatosDemostracion(Connection conexion) throws SQLException {
        try (PreparedStatement sentenciaStand = conexion.prepareStatement("""
                     INSERT OR IGNORE INTO stands
                     (id, ancho, largo, precio_por_metro_cuadrado, estado)
                     VALUES (?, ?, ?, ?, ?)
                     """);
             PreparedStatement sentenciaEditorial = conexion.prepareStatement(
                     "INSERT OR IGNORE INTO editoriales (id, nombre) VALUES (?, ?)");
             PreparedStatement sentenciaReserva = conexion.prepareStatement("""
                     INSERT OR IGNORE INTO reservas
                     (id, editorial_id, stand_id, fecha_creacion, fecha_vencimiento,
                      fecha_limite_liquidacion, precio_total, estado)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                     """)) {
            insertarStandDemostracion(sentenciaStand, 1, "2", "5", "100.00");
            insertarStandDemostracion(sentenciaStand, 2, "3", "4", "100.00");
            insertarEditorialDemostracion(sentenciaEditorial, 1, "Lecturas del Mayab");
            insertarEditorialDemostracion(sentenciaEditorial, 2, "Letras del Sur");

            LocalDate fechaActual = LocalDate.now();
            sentenciaReserva.setInt(1, 1);
            sentenciaReserva.setInt(2, 1);
            sentenciaReserva.setInt(3, 1);
            sentenciaReserva.setString(4, fechaActual.toString());
            sentenciaReserva.setString(5, fechaActual.plusDays(3).toString());
            sentenciaReserva.setString(6, fechaActual.plusDays(10).toString());
            sentenciaReserva.setString(7, "1000.00");
            sentenciaReserva.setString(8, "RESERVADA");
            sentenciaReserva.executeUpdate();
        }
    }

    private static void insertarStandDemostracion(PreparedStatement sentencia, int identificador,
                                                   String ancho, String largo, String precio)
            throws SQLException {
        sentencia.setInt(1, identificador);
        sentencia.setString(2, ancho);
        sentencia.setString(3, largo);
        sentencia.setString(4, precio);
        sentencia.setString(5, "DISPONIBLE");
        sentencia.executeUpdate();
    }

    private static void insertarEditorialDemostracion(PreparedStatement sentencia, int identificador,
                                                       String nombre) throws SQLException {
        sentencia.setInt(1, identificador);
        sentencia.setString(2, nombre);
        sentencia.executeUpdate();
    }
}
