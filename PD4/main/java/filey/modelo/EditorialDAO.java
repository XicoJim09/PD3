package filey.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class EditorialDAO {
    private final Connection conexion;

    public EditorialDAO(Connection conexion) {
        this.conexion = Objects.requireNonNull(conexion);
    }

    public List<EditorialRegistro> listarTodas() throws SQLException {
        String consulta = "SELECT id, nombre FROM editoriales ORDER BY id";
        List<EditorialRegistro> editoriales = new ArrayList<>();
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta);
             ResultSet resultados = sentencia.executeQuery()) {
            while (resultados.next()) {
                editoriales.add(new EditorialRegistro(
                        resultados.getInt("id"), resultados.getString("nombre")));
            }
        }
        return List.copyOf(editoriales);
    }
}
