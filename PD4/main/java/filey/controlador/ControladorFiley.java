package filey.controlador;

import filey.modelo.EditorialDAO;
import filey.modelo.EditorialRegistro;
import filey.modelo.ReservaDAO;
import filey.modelo.ReservaRegistro;
import filey.modelo.StandDAO;
import filey.modelo.StandRegistro;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public final class ControladorFiley {
    private final StandDAO standDAO;
    private final EditorialDAO editorialDAO;
    private final ReservaDAO reservaDAO;

    public ControladorFiley(Connection conexion) {
        Objects.requireNonNull(conexion);
        standDAO = new StandDAO(conexion);
        editorialDAO = new EditorialDAO(conexion);
        reservaDAO = new ReservaDAO(conexion);
    }

    public List<StandRegistro> listarStands() throws SQLException {
        return standDAO.listarTodos();
    }

    public List<EditorialRegistro> listarEditoriales() throws SQLException {
        return editorialDAO.listarTodas();
    }

    public List<ReservaRegistro> listarReservas() throws SQLException {
        return reservaDAO.listarTodas();
    }
}
