package filey.datos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Reserva {
    private final int identificadorReserva;
    private final Editorial editorialResponsable;
    private final Stand standReservado;
    private final LocalDate fechaCreacionReserva;
    private final LocalDate fechaVencimiento;
    private final LocalDate fechaLimiteLiquidacion;
    private final BigDecimal importeTotal;
    private final List<Pago> pagosRegistrados = new ArrayList<>();
    private boolean reservaVencida;

    Reserva(int identificadorReserva, Editorial editorialResponsable, Stand standReservado,
            LocalDate fechaCreacionReserva, LocalDate fechaVencimiento,
            LocalDate fechaLimiteLiquidacion) {
        if (identificadorReserva <= 0) { throw new IllegalArgumentException("El ID debe ser positivo."); }
        this.identificadorReserva = identificadorReserva;
        this.editorialResponsable = Objects.requireNonNull(editorialResponsable);
        this.standReservado = Objects.requireNonNull(standReservado);
        this.fechaCreacionReserva = Objects.requireNonNull(fechaCreacionReserva);
        this.fechaVencimiento = Objects.requireNonNull(fechaVencimiento);
        this.fechaLimiteLiquidacion = Objects.requireNonNull(fechaLimiteLiquidacion);
        if (fechaVencimiento.isBefore(fechaCreacionReserva)
                || fechaVencimiento.isAfter(fechaLimiteLiquidacion)) {
            throw new IllegalArgumentException("Las fechas de la reserva no son válidas.");
        }
        this.importeTotal = standReservado.calcularPrecioTotal();
    }

    public int obtenerIdentificador() { return identificadorReserva; }
    public Editorial obtenerEditorial() { return editorialResponsable; }
    public Stand obtenerStand() { return standReservado; }
    public LocalDate obtenerFechaCreacion() { return fechaCreacionReserva; }
    public LocalDate obtenerFechaVencimiento() { return fechaVencimiento; }
    public LocalDate obtenerFechaLimiteLiquidacion() { return fechaLimiteLiquidacion; }
    public BigDecimal obtenerImporteTotal() { return importeTotal; }
    public List<Pago> obtenerPagosRegistrados() { return List.copyOf(pagosRegistrados); }
    public boolean estaVencida() { return reservaVencida; }

    public BigDecimal calcularTotalAbonado() {
        return pagosRegistrados.stream().map(Pago::obtenerImporte)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
    public BigDecimal calcularSaldoPendiente() { return importeTotal.subtract(calcularTotalAbonado()); }
    public BigDecimal calcularPorcentajePagado() {
        return calcularTotalAbonado().multiply(new BigDecimal("100"))
                .divide(importeTotal, 2, RoundingMode.HALF_UP);
    }
    public boolean cumplePagoMinimo() {
        // Comparación exacta: el porcentaje mostrado redondeado no decide el estado.
        return calcularTotalAbonado().multiply(new BigDecimal("100"))
                .compareTo(importeTotal.multiply(new BigDecimal("15"))) >= 0;
    }

    public void agregarPago(BigDecimal importePago, LocalDate fechaPago) {
        Pago pagoRegistrado = new Pago(importePago, fechaPago);
        if (fechaPago.isBefore(fechaCreacionReserva) || (!pagosRegistrados.isEmpty()
                && fechaPago.isBefore(pagosRegistrados.get(pagosRegistrados.size() - 1).obtenerFecha()))) {
            throw new IllegalArgumentException("No se admiten pagos anteriores a la reserva o al último pago.");
        }
        if (fechaPago.isAfter(fechaLimiteLiquidacion) || reservaVencida
                || (!cumplePagoMinimo() && fechaPago.isAfter(fechaVencimiento))) {
            throw new IllegalStateException("El plazo de pago ha vencido.");
        }
        if (pagoRegistrado.obtenerImporte().compareTo(calcularSaldoPendiente()) > 0) {
            throw new IllegalArgumentException("El pago supera el saldo pendiente.");
        }
        pagosRegistrados.add(pagoRegistrado);
        if (standReservado.obtenerEstadoActual() == EstadoStand.RESERVADO && cumplePagoMinimo()) {
            standReservado.confirmarOcupacionDelStand(this);
        }
    }

    public boolean marcarVencidaSiCorresponde(LocalDate fechaConsulta) {
        Objects.requireNonNull(fechaConsulta);
        if (!reservaVencida && !cumplePagoMinimo() && fechaConsulta.isAfter(fechaVencimiento)) {
            reservaVencida = true;
            standReservado.liberarReserva(this);
            return true;
        }
        return false;
    }
}
