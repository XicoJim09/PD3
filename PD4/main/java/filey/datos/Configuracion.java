package filey.datos;

import java.time.LocalDate;
import java.util.Objects;

/** Valores elegidos al crear el sistema; las fechas límite son inclusivas. */
public final class Configuracion {
    private final LocalDate fechaLimiteDescuento;
    private final LocalDate fechaLimiteLiquidacion;
    private final LocalDate fechaLimiteReserva;
    private final int plazoPagoReservaEnDias;
    private final int diasAnticipacionNotificacion;

    public Configuracion(LocalDate fechaLimiteDescuento, LocalDate fechaLimiteLiquidacion,
                         LocalDate fechaLimiteReserva, int plazoPagoReservaEnDias,
                         int diasAnticipacionNotificacion) {
        this.fechaLimiteDescuento = Objects.requireNonNull(fechaLimiteDescuento);
        this.fechaLimiteLiquidacion = Objects.requireNonNull(fechaLimiteLiquidacion);
        this.fechaLimiteReserva = Objects.requireNonNull(fechaLimiteReserva);
        if (fechaLimiteDescuento.isAfter(fechaLimiteLiquidacion)
                || fechaLimiteReserva.isAfter(fechaLimiteLiquidacion)
                || plazoPagoReservaEnDias <= 0 || diasAnticipacionNotificacion < 0) {
            throw new IllegalArgumentException("La configuración de plazos no es válida.");
        }
        this.plazoPagoReservaEnDias = plazoPagoReservaEnDias;
        this.diasAnticipacionNotificacion = diasAnticipacionNotificacion;
    }

    public LocalDate obtenerFechaLimiteDescuento() { return fechaLimiteDescuento; }
    public LocalDate obtenerFechaLimiteLiquidacion() { return fechaLimiteLiquidacion; }
    public LocalDate obtenerFechaLimiteReserva() { return fechaLimiteReserva; }
    public int obtenerPlazoPagoReservaEnDias() { return plazoPagoReservaEnDias; }
    public int obtenerDiasAnticipacionNotificacion() { return diasAnticipacionNotificacion; }
}
