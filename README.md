# Prices Service

Servicio REST que consulta el precio aplicable a un producto de una cadena en una fecha determinada, desempatando tarifas solapadas mediante el campo `PRIORITY`.

## Stack técnico

- Java 17
- Spring Boot 4.1.1
- Spring Data JPA + Hibernate
- H2 (base de datos en memoria)
- Lombok
- MapStruct
- JUnit 5 + Mockito + AssertJ + MockMvc

## Cómo ejecutar la aplicación

```bash
./mvnw.cmd spring-boot:run
```

La app arranca en `http://localhost:8080`. La consola de H2 está disponible en `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:pricesdb;DB_CLOSE_DELAY=-1`, usuario `sa`, sin contraseña).

## Cómo ejecutar los tests

```bash
./mvnw.cmd test
```

Incluye tests unitarios y de integración (`*IT`) contra los 5 escenarios del enunciado.

## Endpoint

GET /prices?applicationDate={fecha}&productId={id}&brandId={id}

**Parámetros:**

| Parámetro | Tipo | Formato | Ejemplo |
|---|---|---|---|
| `applicationDate` | `LocalDateTime` | ISO-8601 | `2020-06-14T16:00:00` |
| `productId` | `Long` | — | `35455` |
| `brandId` | `Long` | — | `1` |

**Respuesta (200 OK):**

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "curr": "EUR"
}
```

**Respuesta si no hay tarifa aplicable (404 Not Found):**

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "No se ha encontrado ninguna tarifa aplicable para el producto 35455, cadena 1 en la fecha ..."
}
```

## Decisiones de diseño

- **Formato de fecha de entrada**: el enunciado usa `yyyy-MM-dd-HH.mm.ss` como formato de *almacenamiento* de ejemplo; el endpoint acepta **ISO-8601** (`yyyy-MM-ddTHH:mm:ss`) como parámetro de entrada, por ser el estándar de facto en APIs REST y el que Spring parsea de forma nativa sin configuración adicional.

- **Desambiguación por prioridad**: cuando varias tarifas se solapan para una misma fecha, se aplica la de mayor `PRIORITY`. La query del repositorio ordena por `priority DESC`, y el servicio se queda con la primera del resultado.

- **Empate de prioridad**: los datos de ejemplo no contemplan ningún caso de empate de prioridad en fechas solapadas, por lo que no se ejercita en los tests.

- **Arquitectura por capas**: `controller` → `service` (interfaz + implementación) → `repository` → `model`, con `dto`, `mapper` y `exception` como paquetes de soporte. Se separa el contrato del servicio de su implementación para facilitar el testeo y mantener bajo acoplamiento.

- **Mapeo entidad → DTO**: se usa MapStruct en vez de mapeo manual, generando la implementación en tiempo de compilación y evitando código boilerplate repetitivo, alineado con las prácticas de un proyecto de mayor escala.

- **Manejo de errores**: `PriceNotFoundException` se traduce a un `404 Not Found` mediante un `@RestControllerAdvice` centralizado (`GlobalExceptionHandler`), en lugar de dejar que se propague como un `500 Internal Server Error` genérico.

- **`PriceResponse` como `record`**: al ser un DTO de solo salida, se aprovecha la inmutabilidad y el código reducido que ofrecen los `records` de Java.

## Tests

| Test | Escenario | Resultado esperado |
|---|---|---|
| 1 | 14/06 10:00 | Tarifa 1 (35.50 €) |
| 2 | 14/06 16:00 | Tarifa 2 (25.45 €) — mayor prioridad |
| 3 | 14/06 21:00 | Tarifa 1 (35.50 €) |
| 4 | 15/06 10:00 | Tarifa 3 (30.50 €) — mayor prioridad |
| 5 | 16/06 21:00 | Tarifa 4 (38.95 €) — mayor prioridad |