package filey.datos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public final class Stand {
    private final int identificadorStand;
    private final BigDecimal anchoEnMetros;
    private final BigDecimal largoEnMetros;
    private final BigDecimal precioPorMetroCuadrado;
    private EstadoStand estadoActual = EstadoStand.DISPONIBLE;
    private Reserva reservaVigente;

    public Stand(int identificadorStand, BigDecimal anchoEnMetros, BigDecimal largoEnMetros,
                 BigDecimal precioPorMetroCuadrado) {
        if (identificadorStand <= 0 || Objects.requireNonNull(anchoEnMetros).signum() <= 0
                || Objects.requireNonNull(largoEnMetros).signum() <= 0
                || Objects.requireNonNull(precioPorMetroCuadrado).signum() <= 0) {
            throw new IllegalArgumentException("ID, dimensiones y precio deben ser positivos.");
        }
        this.identificadorStand = identificadorStand;
        this.anchoEnMetros = anchoEnMetros;
        this.largoEnMetros = largoEnMetros;
        this.precioPorMetroCuadrado = precioPorMetroCuadrado.setScale(2, RoundingMode.UNNECESSARY);
        if (calcularPrecioTotal().signum() <= 0) {
            throw new IllegalArgumentException("El precio total debe ser al menos un centavo.");
        }
    }

    public int obtenerIdentificador() { return identificadorStand; }
    public BigDecimal obtenerAnchoEnMetros() { return anchoEnMetros; }
    public BigDecimal obtenerLargoEnMetros() { return largoEnMetros; }
    public BigDecimal obtenerPrecioPorMetroCuadrado() { return precioPorMetroCuadrado; }
    public EstadoStand obtenerEstadoActual() { return estadoActual; }
    public BigDecimal calcularSuperficie() { return anchoEnMetros.multiply(largoEnMetros); }
    public BigDecimal calcularPrecioTotal() {
        return calcularSuperficie().multiply(precioPorMetroCuadrado).setScale(2, RoundingMode.HALF_UP);
    }

    public void marcarComoBloqueado() {
        exigirEstadoEsperado(EstadoStand.DISPONIBLE);
        estadoActual = EstadoStand.BLOQUEADO;
    }

    public void quitarBloqueo() {
        exigirEstadoEsperado(EstadoStand.BLOQUEADO);
        estadoActual = EstadoStand.DISPONIBLE;
    }

    /** La reserva solo se construye desde el stand, evitando asociaciones duplicadas. */
    public Reserva crearReserva(int identificadorReserva, Editorial editorialResponsable,
                                LocalDate fechaCreacion, LocalDate fechaVencimiento,
                                LocalDate fechaLimiteLiquidacion) {
        exigirEstadoEsperado(EstadoStand.DISPONIBLE);
        Reserva nuevaReserva = new Reserva(identificadorReserva, editorialResponsable, this,
                fechaCreacion, fechaVencimiento, fechaLimiteLiquidacion);
        reservaVigente = nuevaReserva;
        estadoActual = EstadoStand.RESERVADO;
        return nuevaReserva;
    }

    // Acceso de paquete: únicamente Reserva coordina estas transiciones.
    void confirmarOcupacionDelStand(Reserva reservaEnCurso) {
        exigirReservaVigente(reservaEnCurso);
        exigirEstadoEsperado(EstadoStand.RESERVADO);
        if (!reservaEnCurso.cumplePagoMinimo()) {
            throw new IllegalStateException("No se ha cubierto el 15%.");
        }
        estadoActual = EstadoStand.OCUPADO;
    }

    void liberarReserva(Reserva reservaEnCurso) {
        exigirReservaVigente(reservaEnCurso);
        exigirEstadoEsperado(EstadoStand.RESERVADO);
        if (!reservaEnCurso.estaVencida()) {
            throw new IllegalStateException("La reserva no ha vencido.");
        }
        reservaVigente = null;
        estadoActual = EstadoStand.DISPONIBLE;
    }

    private void exigirEstadoEsperado(EstadoStand estadoEsperado) {
        if (estadoActual != estadoEsperado) {
            throw new IllegalStateException("Stand " + identificadorStand
                    + ": se esperaba " + estadoEsperado + ", estado " + estadoActual);
        }
    }

    private void exigirReservaVigente(Reserva reservaEnCurso) {
        if (reservaVigente != reservaEnCurso) {
            throw new IllegalStateException("La reserva no corresponde a la asignación actual.");
        }
    }
}
