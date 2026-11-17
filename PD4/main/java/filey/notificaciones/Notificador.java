package filey.notificaciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Sujeto concreto; cada editorial recibe únicamente sus propios avisos. */
public final class Notificador implements Sujeto {
    private final Map<Integer, List<Observador>> observadoresPorEditorial = new HashMap<>();

    @Override
    public void registrarObservador(int identificadorEditorial, Observador observador) {
        if (identificadorEditorial <= 0) { throw new IllegalArgumentException("El ID debe ser positivo."); }
        Objects.requireNonNull(observador);
        List<Observador> observadoresEditorial = observadoresPorEditorial.computeIfAbsent(
                identificadorEditorial, identificador -> new ArrayList<>());
        if (!observadoresEditorial.contains(observador)) { observadoresEditorial.add(observador); }
    }

    @Override
    public void eliminarObservador(int identificadorEditorial, Observador observador) {
        List<Observador> observadoresEditorial = observadoresPorEditorial.get(identificadorEditorial);
        if (observadoresEditorial != null) {
            observadoresEditorial.remove(observador);
            if (observadoresEditorial.isEmpty()) { observadoresPorEditorial.remove(identificadorEditorial); }
        }
    }

    @Override
    public void enviarNotificacion(int identificadorEditorial, String mensajeNotificacion) {
        Objects.requireNonNull(mensajeNotificacion);
        for (Observador observador : List.copyOf(
                observadoresPorEditorial.getOrDefault(identificadorEditorial, List.of()))) {
            observador.recibirNotificacion(mensajeNotificacion);
        }
    }
}
