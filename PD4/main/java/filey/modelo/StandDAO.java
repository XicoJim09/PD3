package filey.modelo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class StandDAO {
    private final Connection conexion;

    public StandDAO(Connection conexion) {
        this.conexion = Objects.requireNonNull(conexion);
    }

    public List<StandRegistro> listarTodos() throws SQLException {
        String consulta = """
                SELECT id, ancho, largo, precio_por_metro_cuadrado, estado
                FROM stands
                ORDER BY id
                """;
        List<StandRegistro> stands = new ArrayList<>();
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta);
             ResultSet resultados = sentencia.executeQuery()) {
            while (resultados.next()) {
                stands.add(new StandRegistro(
                        resultados.getInt("id"),
                        new BigDecimal(resultados.getString("ancho")),
                        new BigDecimal(resultados.getString("largo")),
                        new BigDecimal(resultados.getString("precio_por_metro_cuadrado")),
                        resultados.getString("estado")));
            }
        }
        return List.copyOf(stands);
    }
}
