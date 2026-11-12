import java.math.BigDecimal;

public class CalcModel {
    // Valor inicial para la multiplicación.
    private static final String INITIAL_VALUE = "1";

    // BigDecimal hace que la calculadora acepte enteros largos y decimales.
    private BigDecimal m_total;

    CalcModel() {
        reset();
    }

    // Retorna el valor de la calculadora a 1.
    public void reset() {
        m_total = new BigDecimal(INITIAL_VALUE);
    }

    // Multiplica el resultado acumulado por el numero ingresado.
    public void multiplyBy(String operand) {
        m_total = m_total.multiply(new BigDecimal(operand));
    }

    // Suma el numero ingresado al resultado acumulado.
    public void add(String operand) {
        m_total = m_total.add(new BigDecimal(operand));
    }

    // Resta el numero ingresado del resultado acumulado.
    public void subtract(String operand) {
        m_total = m_total.subtract(new BigDecimal(operand));
    }

    // Establece el valor de la calculadora.
    public void setValue(String value) {
        m_total = new BigDecimal(value);
    }

    // Devuelve el resultado como texto sin notacion cientifica.
    public String getValue() {
        return m_total.toPlainString();
    }
}
