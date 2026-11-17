package filey.modelo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ReservaDAO {
    private final Connection conexion;

    public ReservaDAO(Connection conexion) {
        this.conexion = Objects.requireNonNull(conexion);
    }

    public List<ReservaRegistro> listarTodas() throws SQLException {
        String consulta = """
                SELECT r.id, r.editorial_id, e.nombre AS nombre_editorial, r.stand_id,
                       r.fecha_creacion, r.fecha_vencimiento, r.fecha_limite_liquidacion,
                       r.precio_total, r.estado
                FROM reservas r
                JOIN editoriales e ON e.id = r.editorial_id
                JOIN stands s ON s.id = r.stand_id
                ORDER BY r.id
                """;
        List<ReservaRegistro> reservas = new ArrayList<>();
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta);
             ResultSet resultados = sentencia.executeQuery()) {
            while (resultados.next()) {
                reservas.add(new ReservaRegistro(
                        resultados.getInt("id"),
                        resultados.getInt("editorial_id"),
                        resultados.getString("nombre_editorial"),
                        resultados.getInt("stand_id"),
                        LocalDate.parse(resultados.getString("fecha_creacion")),
                        LocalDate.parse(resultados.getString("fecha_vencimiento")),
                        LocalDate.parse(resultados.getString("fecha_limite_liquidacion")),
                        new BigDecimal(resultados.getString("precio_total")),
                        resultados.getString("estado")));
            }
        }
        return List.copyOf(reservas);
    }
}
