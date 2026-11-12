# PD3

Calculadora MVC que realiza la suma, resta y multiplicación de números sucesivos.

## Diagrama de clases

```mermaid
classDiagram
    class CalcMVC {
        +main(String[] args) void
    }

    class CalcController {
        -CalcModel m_model
        -CalcView m_view
        ~CalcController(CalcModel model, CalcView view)
    }

    class MultiplyListener {
        +actionPerformed(ActionEvent e) void
    }

    class AddListener {
        +actionPerformed(ActionEvent e) void
    }

    class SubtractListener {
        +actionPerformed(ActionEvent e) void
    }

    class ClearListener {
        +actionPerformed(ActionEvent e) void
    }

    class CalcModel {
        -String INITIAL_VALUE
        -BigDecimal m_total
        ~CalcModel()
        +reset() void
        +multiplyBy(String operand) void
        +add(String operand) void
        +subtract(String operand) void
        +setValue(String value) void
        +getValue() String
    }

    class CalcView {
        -String INITIAL_VALUE
        -JTextField m_userInputTf
        -JTextField m_totalTf
        -JButton m_multiplyBtn
        -JButton m_addBtn
        -JButton m_subtractBtn
        -JButton m_clearBtn
        -CalcModel m_model
        ~CalcView(CalcModel model)
        ~reset() void
        ~getUserInput() String
        ~setTotal(String newTotal) void
        ~showError(String errMessage) void
        ~addMultiplyListener(ActionListener listener) void
        ~addAddListener(ActionListener listener) void
        ~addSubtractListener(ActionListener listener) void
        ~addClearListener(ActionListener listener) void
    }

    CalcMVC ..> CalcModel : crea
    CalcMVC ..> CalcView : crea
    CalcMVC ..> CalcController : crea
    CalcView --> CalcModel : consulta y actualiza
    CalcController --> CalcModel : opera sobre
    CalcController --> CalcView : lee y actualiza
    CalcController *-- MultiplyListener
    CalcController *-- AddListener
    CalcController *-- SubtractListener
    CalcController *-- ClearListener
    MultiplyListener ..|> ActionListener
    AddListener ..|> ActionListener
    SubtractListener ..|> ActionListener
    ClearListener ..|> ActionListener
```
