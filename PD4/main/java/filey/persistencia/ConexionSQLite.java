package filey.persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public final class ConexionSQLite {
    private ConexionSQLite() { }

    public static Connection abrirConexion() throws SQLException, IOException {
        Path rutaBaseDatos = resolverRutaBaseDatos();
        Files.createDirectories(rutaBaseDatos.getParent());
        Connection conexion = DriverManager.getConnection("jdbc:sqlite:" + rutaBaseDatos);
        try {
            try (Statement sentencia = conexion.createStatement()) {
                sentencia.execute("PRAGMA foreign_keys = ON");
            }
            inicializarEsquema(conexion);
            insertarDatosDemostracion(conexion);
            return conexion;
        } catch (SQLException | RuntimeException error) {
            try {
                conexion.close();
            } catch (SQLException errorAlCerrar) {
                error.addSuppressed(errorAlCerrar);
            }
            throw error;
        }
    }

    private static Path resolverRutaBaseDatos() {
        Path directorioActual = Path.of("").toAbsolutePath().normalize();
        Path carpetaProyecto = directorioActual.resolve("PD4");
        if (!Files.isDirectory(carpetaProyecto)
                && Files.isDirectory(directorioActual.resolve("main").resolve("java"))) {
            carpetaProyecto = directorioActual;
        }
        return carpetaProyecto.resolve("filey.db").toAbsolutePath();
    }

    private static void inicializarEsquema(Connection conexion) throws SQLException {
        try (Statement sentencia = conexion.createStatement()) {
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
