# WebComedor (Login + CRUD Reservas)

## Requisitos
- Java 21
- Maven
- MySQL

## 1) Crear BD y data de prueba
Ejecuta el script:

`src/main/resources/db/comedor.sql`

## 2) Configurar conexión
Edita `src/main/resources/application.properties` con tu usuario/clave de MySQL.

## 3) Ejecutar
```bash
mvn spring-boot:run
```

Entrar a:
- http://localhost:8080/

Usuarios de prueba:
- **aalonso / 123456**
- jperez / 123456
- mlopez / 123456

## Reglas
- 1 reserva activa por día por usuario.
- Máximo 10 reservas activas por (fecha, hora).
- Horas disponibles cada 30 minutos (11:00 a 15:00).

## Unit Tests
```bash
mvn test
```
