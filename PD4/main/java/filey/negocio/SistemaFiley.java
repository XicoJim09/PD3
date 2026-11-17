package filey.negocio;

import filey.datos.Configuracion;
import filey.datos.Editorial;
import filey.datos.EstadoStand;
import filey.datos.Reserva;
import filey.datos.Stand;
import filey.notificaciones.Sujeto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class SistemaFiley {
    private final Configuracion parametrosSistema;
    private final Sujeto servicioNotificaciones;
    private final Map<Integer, Stand> standsRegistrados = new LinkedHashMap<>();
    private final Map<Integer, Editorial> editorialesRegistradas = new LinkedHashMap<>();
    private final Map<Integer, Reserva> reservasRegistradas = new LinkedHashMap<>();
    private final Set<String> notificacionesEnviadas = new HashSet<>();
    private int siguienteIdentificadorReserva = 1;

    public SistemaFiley(Configuracion parametrosSistema, Sujeto servicioNotificaciones) {
        this.parametrosSistema = Objects.requireNonNull(parametrosSistema);
        this.servicioNotificaciones = Objects.requireNonNull(servicioNotificaciones);
    }

    public void agregarStand(Stand standNuevo) {
        Objects.requireNonNull(standNuevo);
        if (standNuevo.obtenerEstadoActual() != EstadoStand.DISPONIBLE) {
            throw new IllegalArgumentException("El stand debe registrarse disponible.");
        }
        if (standsRegistrados.putIfAbsent(standNuevo.obtenerIdentificador(), standNuevo) != null) {
            throw new IllegalArgumentException("ID de stand duplicado.");
        }
    }

    public void agregarEditorial(Editorial editorialNueva) {
        Objects.requireNonNull(editorialNueva);
        if (editorialesRegistradas.putIfAbsent(
                editorialNueva.obtenerIdentificador(), editorialNueva) != null) {
            throw new IllegalArgumentException("ID de editorial duplicado.");
        }
        servicioNotificaciones.registrarObservador(editorialNueva.obtenerIdentificador(), editorialNueva);
    }

    public List<Stand> listarStandsDisponibles() {
        return standsRegistrados.values().stream()
                .filter(stand -> stand.obtenerEstadoActual() == EstadoStand.DISPONIBLE).toList();
    }

    public List<Reserva> listarReservas() {
        return List.copyOf(reservasRegistradas.values());
    }

    public Reserva reservarStand(int identificadorEditorial, int identificadorStand, LocalDate fechaReserva) {
        Objects.requireNonNull(fechaReserva);
        Editorial editorialResponsable = editorialesRegistradas.get(identificadorEditorial);
        if (editorialResponsable == null) { throw new IllegalArgumentException("Editorial inexistente."); }
        Stand standElegido = obtenerStandRegistrado(identificadorStand);
        if (fechaReserva.isAfter(parametrosSistema.obtenerFechaLimiteReserva())) {
            throw new IllegalStateException("Se ha superado la fecha límite de reserva.");
        }
        LocalDate fechaVencimiento = fechaReserva.plusDays(parametrosSistema.obtenerPlazoPagoReservaEnDias());
        if (fechaVencimiento.isAfter(parametrosSistema.obtenerFechaLimiteReserva())) {
            fechaVencimiento = parametrosSistema.obtenerFechaLimiteReserva();
        }
        Reserva nuevaReserva = standElegido.crearReserva(siguienteIdentificadorReserva, editorialResponsable,
                fechaReserva, fechaVencimiento, parametrosSistema.obtenerFechaLimiteLiquidacion());
        reservasRegistradas.put(siguienteIdentificadorReserva++, nuevaReserva);
        return nuevaReserva;
    }

    public void agregarPagoReserva(int identificadorReserva, BigDecimal importePago, LocalDate fechaPago) {
        obtenerReservaRegistrada(identificadorReserva).agregarPago(importePago, fechaPago);
    }

    public void bloquearStand(int identificadorStand) {
        obtenerStandRegistrado(identificadorStand).marcarComoBloqueado();
    }

    public void desbloquearStand(int identificadorStand) {
        obtenerStandRegistrado(identificadorStand).quitarBloqueo();
    }

    /** Debe invocarse con la fecha de consulta; no requiere tareas en segundo plano. */
    public void revisarFechasLimite(LocalDate fechaConsulta) {
        Objects.requireNonNull(fechaConsulta);
        for (Reserva reservaActiva : reservasRegistradas.values()) {
            if (fechaConsulta.isBefore(reservaActiva.obtenerFechaCreacion())) { continue; }
            reservaActiva.marcarVencidaSiCorresponde(fechaConsulta);
            if (reservaActiva.estaVencida()
                    || reservaActiva.calcularSaldoPendiente().signum() == 0) { continue; }
            if (!reservaActiva.cumplePagoMinimo()) {
                emitirAvisoSiCorresponde(reservaActiva, "RESERVA",
                        reservaActiva.obtenerFechaVencimiento(), fechaConsulta,
                        "Vence el plazo para cubrir al menos el 15% de la reserva");
            }
            emitirAvisoSiCorresponde(reservaActiva, "DESCUENTO",
                    parametrosSistema.obtenerFechaLimiteDescuento(), fechaConsulta,
                    "Vence el plazo de descuento por pronto pago");
            emitirAvisoSiCorresponde(reservaActiva, "LIQUIDACION",
                    parametrosSistema.obtenerFechaLimiteLiquidacion(), fechaConsulta,
                    "Vence el plazo para liquidar el saldo");
        }
    }

    private void emitirAvisoSiCorresponde(Reserva reservaActiva, String tipoAviso,
                                          LocalDate fechaLimite, LocalDate fechaConsulta,
                                          String descripcionAviso) {
        if (fechaConsulta.isBefore(fechaLimite.minusDays(
                parametrosSistema.obtenerDiasAnticipacionNotificacion()))
                || fechaConsulta.isAfter(fechaLimite)) { return; }
        String identificadorNotificacion = reservaActiva.obtenerIdentificador()
                + ":" + tipoAviso + ":" + fechaLimite;
        if (!notificacionesEnviadas.contains(identificadorNotificacion)) {
            servicioNotificaciones.enviarNotificacion(
                    reservaActiva.obtenerEditorial().obtenerIdentificador(),
                    descripcionAviso + " el " + fechaLimite
                            + " (reserva " + reservaActiva.obtenerIdentificador()
                            + ", stand " + reservaActiva.obtenerStand().obtenerIdentificador() + ").");
            notificacionesEnviadas.add(identificadorNotificacion);
        }
    }

    private Stand obtenerStandRegistrado(int identificadorStand) {
        Stand standEncontrado = standsRegistrados.get(identificadorStand);
        if (standEncontrado == null) { throw new IllegalArgumentException("Stand inexistente."); }
        return standEncontrado;
    }

    private Reserva obtenerReservaRegistrada(int identificadorReserva) {
        Reserva reservaEncontrada = reservasRegistradas.get(identificadorReserva);
        if (reservaEncontrada == null) { throw new IllegalArgumentException("Reserva inexistente."); }
        return reservaEncontrada;
    }
}
