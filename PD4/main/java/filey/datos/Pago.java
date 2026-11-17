package filey.datos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public final class Pago {
    private final BigDecimal importePago;
    private final LocalDate fechaPago;

    public Pago(BigDecimal importePago, LocalDate fechaPago) {
        Objects.requireNonNull(importePago, "El importe es obligatorio.");
        if (importePago.signum() <= 0) {
            throw new IllegalArgumentException("El pago debe ser positivo.");
        }
        // Se rechazan fracciones menores que un centavo; no se redondean pagos.
        this.importePago = importePago.setScale(2, RoundingMode.UNNECESSARY);
        this.fechaPago = Objects.requireNonNull(fechaPago, "La fecha es obligatoria.");
    }

    public BigDecimal obtenerImporte() { return importePago; }
    public LocalDate obtenerFecha() { return fechaPago; }
}
