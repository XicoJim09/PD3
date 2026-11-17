# PD4: stands con JDBC y SQLite

El ejemplo separa las responsabilidades en tres capas:

- **Modelo** (`filey.modelo`): los DAO consultan las tablas con JDBC y devuelven registros tipados.
- **Controlador** (`filey.controlador`): solicita las listas a los DAO.
- **Vista** (`filey.vista`): presenta los registros en consola.

`ConexionSQLite` crea las tablas `stands`, `editoriales` y `reservas`, activa las claves foráneas e inserta datos de demostración si aún no existen. El archivo `filey.db` se crea dentro de `PD4` al ejecutar desde la raíz del repositorio o en el directorio actual al ejecutar desde PD4.

Requiere Java 17 o posterior y Maven. Desde la raíz del repositorio, ejecuta:

```powershell
mvn -f PD4\pom.xml clean compile exec:java
```

La dependencia JDBC de SQLite está declarada en `pom.xml`.
