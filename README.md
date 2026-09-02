# TP1 · Spring Boot, API REST y arquitectura en capas

Punto de partida para el práctico. **No está completo a propósito**: el
módulo de `productos` (consumo de una API externa) está resuelto de punta a
punta como ejemplo del patrón a seguir. El módulo de `favoritos` (CRUD
propio) no tiene nada armado — ni entidad, ni repository, ni DTOs, ni
service, ni controller — se construye entero en clase, reusando exactamente
el mismo patrón que ya se ve en `productos` (y la misma infraestructura
transversal: `GlobalExceptionHandler` y las excepciones ya existen y
alcanzan para los dos módulos).

> ⚠️ **Sin persistencia real.** Cuando se arme el repository de favoritos,
> va a guardar todo en memoria (un `Map`, sin base de datos). Los datos se
> pierden cada vez que se reinicia la aplicación. Eso es intencional — la
> persistencia con JPA se aborda en el TP2, no acá.

## Cómo levantar el proyecto

Requiere Java 25. Usar siempre el wrapper, nunca un `mvn` instalado aparte:

```
# Windows
.\mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```

Cuando el log muestre `Started DemoApplication`, la app queda escuchando en
`http://localhost:8080`.

Para compilar y correr los tests: `./mvnw test` (o `.\mvnw.cmd test`).

## Endpoints disponibles hoy

| Método | Path | Qué hace |
|---|---|---|
| GET | `/health` | Chequeo de salud básico |
| GET | `/ping` | Devuelve `pong`, sin JSON — otro chequeo trivial |
| GET | `/api/productos?limit=&skip=` | Lista paginada del catálogo (consume DummyJSON) |
| GET | `/api/productos/{id}` | Un producto puntual. 404 si no existe |

Documentación interactiva (Swagger UI):
**http://localhost:8080/swagger-ui/index.html**
(el JSON crudo de OpenAPI está en `/v3/api-docs`).

### Probarlo a mano

```
curl "http://localhost:8080/api/productos?limit=2"
curl "http://localhost:8080/api/productos/1"
curl -i "http://localhost:8080/api/productos/999999"   # -> 404 con detalle
```

El caso de error (`404`) responde con un `ProblemDetail` uniforme, por
ejemplo:

```json
{
  "status": 404,
  "title": "Not Found",
  "detail": "No existe el producto con id 999999",
  "instance": "/api/productos/999999"
}
```

## Estructura del proyecto

Organizado **por capa técnica** (no por feature): todo lo que es
"controller" vive junto, todo lo que es "service" vive junto, etc. Así se ve
de un vistazo qué capa le falta a cada recurso.

```
com.example.demo
├── controller/            → @RestController (HTTP in/out, nada de lógica)
│   ├── HealthController
│   ├── PingController
│   ├── ProductoController
│   └── FavoritoController         ⬜ para armar en clase
├── service/               → interfaz + implementación, lógica de negocio
│   ├── ProductoService            (interfaz)
│   ├── ProductoServiceImpl        (mapea DummyJSON -> ProductoDTO)
│   ├── FavoritoService            ⬜ para armar en clase (interfaz)
│   └── FavoritoServiceImpl        ⬜ para armar en clase
├── repository/            → interfaz + implementación, acceso a datos
│   ├── FavoritoRepository          ⬜ para armar en clase (interfaz)
│   └── InMemoryFavoritoRepository  ⬜ para armar en clase (Map en memoria, sin JPA)
├── domain/                → entidades de dominio (no son DTOs)
│   └── Favorito                   ⬜ para armar en clase
├── dto/
│   ├── producto/          → contrato propio de la API (ProductoDTO, ProductoPageResponse)
│   └── favorito/          ⬜ para armar en clase (FavoritoRequest, FavoritoResponse)
├── client/
│   └── dummyjson/         → todo lo que sabe hablar con la API externa
│       ├── DummyJsonProducto           (forma del JSON externo)
│       ├── DummyJsonProductosResponse
│       └── DummyJsonClient             (llamadas HTTP con RestClient)
├── exception/             → manejo uniforme de errores (ya sirve para los dos módulos)
│   ├── RecursoNoEncontradoException  (404)
│   ├── ServicioExternoException      (5xx, falla al consumir DummyJSON)
│   └── GlobalExceptionHandler        (@RestControllerAdvice)
└── config/
    ├── RestClientConfig   → bean de RestClient para DummyJSON
    └── OpenApiConfig      → metadata general de Swagger
```

Las líneas marcadas ⬜ todavía no existen en el repo — son las que se crean
en clase.

La regla clave del módulo de productos: **nadie fuera de `client.dummyjson`
conoce los nombres de campo de DummyJSON** (`title`, `thumbnail`, etc.). El
resto de la app siempre trabaja con `ProductoDTO`, que tiene sus propios
nombres (`nombre`, `imagenUrl`, ...).

## Para hacer en clase: favoritos

No hay nada armado de este módulo — se construye entero, capa por capa,
siguiendo el mismo patrón que ya está resuelto en `productos`. Lo único que
ya existe y sirve para los dos módulos es `GlobalExceptionHandler` y las
excepciones (`RecursoNoEncontradoException`, `ServicioExternoException`).

1. **Dominio** en `domain/`: `Favorito` (record), por ejemplo con
   `id`, `productoId` (referencia al producto externo — no se duplican sus
   datos), `nota` (comentario personal) y `fechaAgregado`.
2. **Repository** en `repository/`: `FavoritoRepository` (interfaz con
   `findAll`, `findById`, `save`, `deleteById`, `existsById`) +
   `InMemoryFavoritoRepository` (un `Map` en memoria, sin JPA — mismo
   patrón que se explicó para el catálogo, pero acá es la fuente de datos
   real del recurso, no un caché).
3. **DTOs** en `dto/favorito/`:
   - `FavoritoRequest` (lo que manda el cliente al crear/actualizar): al
     menos `productoId` y `nota`, con Bean Validation (`@NotNull`, etc.).
   - `FavoritoResponse` (lo que devuelve la API): `id`, `productoId`,
     `nota`, `fechaAgregado`.
4. **Service** en `service/`: `FavoritoService` (interfaz) +
   `FavoritoServiceImpl`, con el CRUD completo y el mapeo
   entidad ↔ DTO. Cuando no se encuentra un favorito, lanzar
   `RecursoNoEncontradoException` — ya existe y ya está manejada por
   `GlobalExceptionHandler`, no hace falta crear una excepción nueva.
5. **Controller** en `controller/`: `FavoritoController` sobre
   `/api/favoritos`, con `@Tag(name = "favoritos")` y un `@Operation` por
   endpoint:

   | Operación | Método | Código de éxito |
   |---|---|---|
   | Crear | `POST /api/favoritos` | `201 Created` |
   | Listar | `GET /api/favoritos` | `200 OK` |
   | Obtener uno | `GET /api/favoritos/{id}` | `200 OK` |
   | Actualizar | `PUT /api/favoritos/{id}` | `200 OK` |
   | Eliminar | `DELETE /api/favoritos/{id}` | `204 No Content` |

6. **Validación**: anotar `FavoritoRequest` con Bean Validation y agregar
   `@Valid` en el parámetro del controller. La respuesta de error (`400`)
   con el detalle de campos ya la arma `GlobalExceptionHandler` — no hay
   que escribir nada nuevo ahí tampoco.

Con eso, Swagger UI va a mostrar los dos grupos de endpoints
(`productos` y `favoritos`) documentados.

## Dependencias

- `spring-boot-starter-webmvc` — Spring MVC + Tomcat embebido.
- `spring-boot-starter-validation` — Bean Validation (`@NotNull`, `@NotBlank`, ...).
- `springdoc-openapi-starter-webmvc-ui` — genera el OpenAPI y sirve Swagger UI.
