package filey.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaRegistro(int identificadorReserva, int identificadorEditorial,
                              String nombreEditorial, int identificadorStand,
                              LocalDate fechaCreacion, LocalDate fechaVencimiento,
                              LocalDate fechaLimiteLiquidacion, BigDecimal precioTotal,
                              String estado) {
}
