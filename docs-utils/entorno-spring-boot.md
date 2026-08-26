# Preparar Spring Boot en VS Code

Guía para instalar Java, generar un proyecto Maven y levantar un servicio Spring Boot mínimo.

**Tiempo estimado:** 25–40 minutos (la mayor parte es descarga).

**Meta:** el entorno está listo cuando `java` y `javac` informan versión 25, Maven muestra `BUILD SUCCESS`, y `GET /health` devuelve un JSON con `status: "ok"` y `service: "api-blank"`.

---

## Qué vas a instalar

| Herramienta | Elección | Para qué sirve |
|---|---|---|
| Editor | Visual Studio Code | Escribir, ejecutar y depurar código |
| Java | Microsoft Build of OpenJDK 25 LTS | VM + compilador |
| Soporte Java | Extension Pack for Java | Lenguaje, depurador, pruebas, Maven |
| Soporte Spring | Spring Boot Extension Pack | Asistencia Spring, Initializr, panel de apps |
| Proyecto | Spring Boot 4.1.x estable | App web + servidor embebido |
| Build | Maven Wrapper | Usa la versión de Maven del proyecto |

> ⚠️ **Por ahora no instales:** Maven global, Tomcat, Gradle, Docker, PostgreSQL, Flyway, Lombok, otro IDE, seguridad, persistencia u OpenAPI. Eso llega cuando el TP lo requiera.

---

## 1. Preparar Visual Studio Code

### Instalar extensiones
1. Abrí VS Code.
2. Abrí **Extensiones**: `Ctrl+Shift+X` (Win/Linux) o `Cmd+Shift+X` (Mac).
3. Instalá **Extension Pack for Java** (Microsoft).
4. Instalá **Spring Boot Extension Pack** (VMware).
5. Si aparece **Reload/Reiniciar**, usalo.

### Instalar el JDK 25
- **Windows (PowerShell):**
  ```
  winget install Microsoft.OpenJDK.25
  ```
- **Alternativa gráfica:** descargar desde [Microsoft Build of OpenJDK](https://learn.microsoft.com/java/openjdk/download#openjdk-25).
- **Desde VS Code:** `Ctrl+Shift+P` / `Cmd+Shift+P` → **Java: Install New JDK** → elegir OpenJDK 25.
- Cerrá y volvé a abrir PowerShell y VS Code al terminar.

### Comprobar la instalación
Abrí una terminal **nueva** en VS Code (`Terminal → Nueva terminal`):
```
java --version
javac --version
```
Ambas salidas deben empezar con `25`. Si no, ver [Problemas frecuentes](#problemas-frecuentes).

---

## 2. Generar el proyecto base

Usar [Spring Initializr](https://start.spring.io/):

| Campo | Valor |
|---|---|
| Project | **Maven** |
| Language | **Java** |
| Spring Boot | **4.1.x** (estable más reciente, sin SNAPSHOT/M1/M2) |
| Group | `ar.edu.unvime` |
| Artifact | `api-blank` |
| Name | `api-blank` |
| Description | `Servicio base para Web II` |
| Package name | `ar.edu.unvime.apiblank` |
| Packaging | **Jar** |
| Java | **25** |

1. **Add Dependencies** → buscar y agregar únicamente **Spring Web**.
2. **Generate**.
3. Descomprimir el `.zip` en una carpeta simple (ej. `Documentos/web2/api-blank`). No trabajar dentro del zip.

### Estructura esperada
```
api-blank/
├── .mvn/wrapper/
├── src/
│   ├── main/
│   │   ├── java/ar/edu/unvime/apiblank/
│   │   │   └── ApiBlankApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── mvnw
├── mvnw.cmd
└── pom.xml
```

### Qué revisar en `pom.xml`
Es el archivo central del proyecto Maven: define versión de Spring Boot, versión de Java y dependencias.

```xml
<properties>
    <java.version>25</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>
    <!-- Initializr también agrega las dependencias de prueba -->
</dependencies>
```
⚠️ En Spring Boot 4.1 la dependencia es `spring-boot-starter-webmvc` (no `spring-boot-starter-web`, como en tutoriales viejos). No la reemplaces.

### Abrir la carpeta correcta
**Archivo → Abrir carpeta** → seleccionar la carpeta `api-blank` que contiene `pom.xml` (no un `.java` suelto ni la carpeta superior `web2`). Esperar a que termine la importación de Java (indicador en la barra inferior) sin errores.

---

## 3. Comprobar Maven

Usar siempre el **wrapper**, nunca `mvn` directo:

- **Windows:** `.\mvnw.cmd --version` y `.\mvnw.cmd test`
- **macOS/Linux:** `./mvnw --version` y `./mvnw test`

Al final de `test` debe aparecer `BUILD SUCCESS`.

---

## 4. Agregar un endpoint mínimo

Crear `src/main/java/ar/edu/unvime/apiblank/web/HealthController.java`:

```java
package ar.edu.unvime.apiblank.web;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "ok",
            "service", "api-blank"
        );
    }
}
```

- `@RestController`: la clase atiende HTTP y devuelve datos.
- `@GetMapping("/health")`: conecta `GET /health` con el método.
- El `Map` se serializa automáticamente como JSON.

No tocar `ApiBlankApplication.java`.

---

## 5. Levantar el servicio

- **Windows:** `.\mvnw.cmd spring-boot:run`
- **macOS/Linux:** `./mvnw spring-boot:run`

Cuando el log muestre `Tomcat started on port 8080` y `Started ApiBlankApplication`, abrir `http://localhost:8080/health`. Debe responder:
```json
{
  "status": "ok",
  "service": "api-blank"
}
```

Verificación alternativa (segunda terminal, Windows):
```
Invoke-RestMethod http://localhost:8080/health
```

Para detener: `Ctrl+C` en la terminal donde corre el proceso.

---

## Comandos que vas a volver a usar

| Tarea | Windows | macOS/Linux |
|---|---|---|
| Ver versión de Maven | `.\mvnw.cmd --version` | `./mvnw --version` |
| Compilar y testear | `.\mvnw.cmd test` | `./mvnw test` |
| Levantar en desarrollo | `.\mvnw.cmd spring-boot:run` | `./mvnw spring-boot:run` |
| Generar `.jar` | `.\mvnw.cmd package` | `./mvnw package` |
| Detener servicio | `Ctrl+C` | `Ctrl+C` |

---

## Problemas frecuentes

**`java` o `javac` no se reconoce**
1. Cerrá todas las terminales y reiniciá VS Code.
2. Reintentá `java --version` / `javac --version` en terminal nueva.
3. `Ctrl+Shift+P` → **Java: Configure Java Runtime** → verificar JDK 25.
4. Si no aparece, reinstalar el JDK con configuración predeterminada de PATH.

**VS Code muestra muchos errores en archivos que no tocaste**
Confirmar que se abrió la carpeta exacta con `pom.xml`. Si persiste: paleta de comandos → **Java: Clean Java Language Server Workspace** → reiniciar.

**PowerShell dice que `mvnw` no existe**
El comando empieza con `.\`: `.\mvnw.cmd`. La terminal debe estar en la misma carpeta que `mvnw.cmd` y `pom.xml`.

**macOS/Linux: `Permission denied`**
```
chmod +x mvnw
./mvnw test
```

**Maven no puede descargar archivos**
Necesita conexión a internet estable la primera vez. Instalar Maven global no soluciona un problema de red.

**El puerto 8080 ya está ocupado**
Detener el otro proceso con `Ctrl+C`, o agregar temporalmente en `application.properties`:
```
server.port=8081
```
y usar `http://localhost:8081/health`.

---

## Lista final de control

- [ ] `java --version` → Java 25
- [ ] `javac --version` → Java 25
- [ ] VS Code con Extension Pack for Java y Spring Boot Extension Pack
- [ ] Carpeta abierta contiene `pom.xml`, `mvnw`, `mvnw.cmd`
- [ ] Proyecto: Maven, Spring Boot 4.1.x, Java 25, solo Spring Web
- [ ] `test` termina con `BUILD SUCCESS`
- [ ] `GET http://localhost:8080/health` devuelve el JSON esperado
- [ ] Sabés detener el servicio con `Ctrl+C`

---

## Documentación oficial

- [Requisitos de Spring Boot 4.1](https://docs.spring.io/spring-boot/system-requirements.html)
- [Spring Quickstart](https://spring.io/quickstart/)
- [Spring Boot en Visual Studio Code](https://code.visualstudio.com/docs/java/java-spring-boot)
- [Instalar Microsoft Build of OpenJDK](https://learn.microsoft.com/java/openjdk/install)
- [Maven Wrapper](https://maven.apache.org/tools/mavenwrapper.html)
- [Ejecutar Spring Boot con Maven](https://docs.spring.io/spring-boot/maven-plugin/run.html)
- [Aplicaciones web con Spring MVC](https://docs.spring.io/spring-boot/reference/web/servlet.html)

*Fuente: guía "Preparar Spring Boot en VS Code" — UNVIME, revisada 17/08/2026.*
