package filey.modelo;

import java.math.BigDecimal;

public record StandRegistro(int identificadorStand, BigDecimal anchoEnMetros, BigDecimal largoEnMetros,
                            BigDecimal precioPorMetroCuadrado, String estado) {
}
