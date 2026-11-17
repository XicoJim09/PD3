package filey.persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionSQLite {
    private ConexionSQLite() { }

    public static Connection abrirConexion() throws SQLException, IOException {
        Path rutaBaseDatos = resolverRutaBaseDatos();
        Files.createDirectories(rutaBaseDatos.getParent());
        return DriverManager.getConnection("jdbc:sqlite:" + rutaBaseDatos);
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
}
