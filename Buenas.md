# Guía general de buenas prácticas y preferencias de código

Esta guía define cómo organizar y explicar cambios de código en cualquier proyecto. Primero se debe respetar lo que la persona solicita y la estructura que ya existe. Una instrucción nueva y explícita tiene prioridad sobre una regla general de este documento.

Los ejemplos usan Java, Spring Boot y MyBatis porque reflejan el estilo solicitado en proyectos anteriores. Si el proyecto usa otras tecnologías, adaptar las mismas ideas sin imponer Java, Spring o MyBatis.

## 1. Objetivo general

- El código debe ser fácil de leer, encontrar y cambiar.
- Mantener una solución sencilla, completa y ordenada. Sencilla no significa omitir validaciones o respuestas de error necesarias.
- No agregar archivos, clases, capas, dependencias o carpetas solo porque un patrón las permita. Cada pieza nueva debe cumplir una función concreta y ser necesaria para la petición.
- Antes de inventar una estructura, revisar cómo está organizado el proyecto y continuar su patrón.
- Entregar el cambio solicitado completo. No detenerse después de proponer qué se podría hacer.
- No alterar otras partes del proyecto que no sean necesarias para el cambio.

## 2. Carpetas, archivos y documentación

- No crear carpetas nuevas salvo que se solicite o la estructura existente las requiera claramente.
- Agregar archivos cuando cada uno tenga una responsabilidad concreta. Evitar una colección de archivos de una sola función que obligue a saltar entre ellos para entender una operación.
- Mantener juntas las operaciones de un mismo recurso cuando eso las haga fáciles de encontrar: por ejemplo, un controller para las rutas de sucursales y otro para las de trabajadores.
- Conservar el README que ya exista. No reemplazarlo por documentación generada sin que se pida.
- No crear archivos `AGENTS.md` sin petición expresa.
- Respetar la ubicación de pruebas que ya establezca el proyecto. No trasladarlas ni inventar otra ubicación.
- Una preferencia específica de un proyecto no se copia automáticamente a todos los proyectos. Por ejemplo, la ruta de pruebas acordada para un proyecto no obliga a usarla en otro.

## 3. Nombres y formato

- Usar nombres que describan la responsabilidad real: `SucursalService`, `TrabajadorMapper`, `CrearTrabajadorDTO`.
- En Java y JSON, escribir propiedades en camelCase: `idSucursal`, `idTrabajador`, `idPais`.
- No usar guion bajo en propiedades Java o JSON. Mantener los nombres originales de columnas de base de datos cuando ya existen, como `id_sucursal` e `id_pais`.
- Escribir cada importación de forma explícita; no usar importaciones comodín.
- En SQL, escribir las columnas que la operación usa; no usar `SELECT *`.
- No cambiar nombres de columnas o tablas existentes solo para alinearlos con Java. Hacer la correspondencia en el mapeo.
- Mantener el formato del código y los nombres de paquetes existentes. No mezclar idiomas en los identificadores sin una razón del proyecto.

| Base de datos | Propiedad Java/JSON |
|---|---|
| `id_sucursal` | `idSucursal` |
| `id_trabajador` | `idTrabajador` |
| `id_pais` | `idPais` |
| `inicio_jornada` | `inicioJornada` |

## 4. Arquitectura y DTO

Cuando se haya pedido una arquitectura por capas, seguir este recorrido:

```text
Petición HTTP → Controller → Service → Mapper/Repository → Base de datos
                                      ↓
                                  DTO/modelo
```

- **Controller:** recibe la ruta, parámetros y JSON; valida el formato HTTP básico; delega al servicio y devuelve el código HTTP y la respuesta. No debe contener SQL ni reglas de negocio extensas.
- **Service:** implementa las reglas de negocio, coordina validaciones y llamadas de persistencia.
- **Mapper/Repository:** accede a la base de datos y declara qué datos necesita cada operación.
- **DTO:** define los datos que una operación recibe o devuelve entre capas. Usar DTO de forma consistente para que la API no dependa directamente de la forma interna de las tablas.
- **Modelo/entidad:** representa datos del dominio o de la persistencia. No asumir que DTO y entidad siempre son lo mismo.
- Mantener pocos DTO claros. Crear DTO de solicitud y respuesta distintos cuando de verdad tengan campos o propósitos diferentes; no generar varios DTO casi idénticos para cada método.
- No introducir una capa de servicio vacía que solo reenvía cada llamada sin aportar responsabilidad. Si el proyecto ya pide Controller → Service → Mapper, mantener esa capa y colocar allí las reglas correspondientes.
- Reutilizar validaciones fuera del controller. Mantenerlas simples y con mensajes que permitan corregir la petición.
- No encadenar validadores, excepciones y conversores para una comprobación sencilla.

### Ejemplo de DTO

```java
package com.ejemplo.sucursal.dto;

import lombok.Data;

@Data
public class CrearSucursalDTO {
    private Integer idPais;
    private String nombre;
    private String hora;
}
```

Con este DTO queda claro qué campos acepta la operación de creación. El controller lo recibe, el servicio lo valida y coordina la creación, y el mapper/XML muestra cómo se guardan esos datos. No debe ser necesario adivinar los campos examinando varias clases.

## 5. Controller CRUD

- Agrupar en un controller las rutas HTTP de un mismo recurso.
- Incluir las operaciones que pidió el alcance: crear, listar/buscar, actualizar y eliminar. No agregar endpoints adicionales sin motivo.
- Usar rutas y verbos HTTP coherentes: GET consulta, POST crea, PUT reemplaza/actualiza, PATCH cambia parcialmente y DELETE elimina.
- El controller debe ser claro y completo: parámetros, DTO recibido, validación básica, llamada al servicio y respuesta. Evitar tanto lógica de negocio extensa como métodos opacos que no muestren qué ocurre.
- Mantener el manejo de errores cerca del CRUD cuando así esté organizado el proyecto. Atender JSON incorrecto, validación, inexistencia, conflictos y errores de persistencia.
- No usar `System.out.println` como registro permanente de operaciones. Usar el sistema de logs del proyecto si hace falta registrar un evento.
- No devolver `Map<String, Object>` sin una razón concreta cuando una respuesta DTO tipada describe mejor los datos.

### Ejemplo del recorrido de una petición

Para `GET /api/sucursales/7`:

1. El controller recibe `idSucursal = 7`.
2. El controller delega la búsqueda al servicio.
3. El servicio valida la regla necesaria y llama al mapper.
4. El mapper ejecuta el SELECT y MyBatis construye el modelo con el resultado.
5. Si no hay fila, responder 404. Si hay fila, devolver la respuesta tipada como JSON.

Cada paso debe poder localizarse por su responsabilidad; no hay que repartirlo entre archivos genéricos que no aportan claridad.

## 6. Validación y errores HTTP

Usar códigos HTTP que describan el resultado:

| Situación | Respuesta habitual |
|---|---:|
| Creación correcta | 201 Created |
| Consulta o actualización correcta | 200 OK |
| Eliminación correcta sin cuerpo | 204 No Content |
| JSON, parámetro o campo inválido | 400 Bad Request |
| Registro inexistente | 404 Not Found |
| Dato duplicado o restricción de integridad | 409 Conflict |
| Error inesperado de persistencia | 500 Internal Server Error |

Ejemplos:

- DNI duplicado al crear un trabajador → 409.
- Buscar un DNI inexistente → 404.
- Enviar un identificador que debe ser positivo con valor cero → 400.
- No exponer al cliente SQL, credenciales, trazas ni detalles internos de la base de datos.
- Responder con un mensaje claro y apropiado para el usuario de la API.
- Reutilizar el mecanismo de errores que ya existe. No crear un sistema global de excepciones y múltiples DTO de error si los handlers actuales bastan.

## 7. MyBatis y el patrón de mapper preferido

Cuando el proyecto use MyBatis, seguir el estilo de mapper basado en interfaz Java y XML mapper:

- La interfaz Java declara los métodos y está marcada con `@Mapper`.
- El XML declara el namespace de esa interfaz.
- El `id` de cada statement XML debe coincidir exactamente con el nombre del método Java que lo ejecuta.
- Mantener en el XML de cada entidad su `resultMap`, los fragmentos reutilizables de SQL y sus operaciones CRUD. Así se entiende una tabla en su mapper.
- Colocar SQL en XML cuando ese sea el patrón escogido. No duplicar una operación en anotación Java y XML.
- Mostrar explícitamente las columnas de SELECT, INSERT y UPDATE. Poder leer el XML debe permitir saber qué campos consulta o modifica la operación.
- Nombrar los argumentos con `@Param` cuando la interfaz recibe varios valores, y usar el mismo nombre dentro de `#{...}`.
- Usar parámetros `#{...}` para valores. No concatenar directamente texto de la petición en SQL.
- No ocultar los campos del INSERT detrás de una llamada que haga imposible saber qué espera la persistencia. Si se pasa un DTO/modelo como objeto, la lista de columnas y cada `#{propiedad}` deben estar visibles en el XML.
- Seguir los nombres de método del mapper existente. No cambiar a nombres de un ejemplo genérico si la interfaz del proyecto define otros.

### Uso de BaseMapper

El estilo que extiende una interfaz base puede ser adecuado:

```java
@Mapper
public interface SucursalMapper extends BaseMapper<Sucursal> {
}
```

Pero `BaseMapper` no aparece automáticamente por agregar MyBatis básico. Antes de usarlo:

1. Comprobar que la dependencia del proyecto contiene esa clase.
2. Leer la interfaz para conocer los métodos y sus parámetros reales.
3. Comprobar qué XML/proveedor implementa las operaciones heredadas.
4. Usar ese patrón solo si de verdad permite entender y ejecutar el CRUD con menos repetición.

No crear un `BaseMapper` propio o agregar otra biblioteca sin necesidad y sin explicar qué aporta.

### Ejemplo de interfaz mapper

Si no se usa `BaseMapper`, la interfaz puede declarar directamente el método que implementa el XML:

```java
package com.ejemplo.sucursal.mapper;

import com.ejemplo.sucursal.model.Sucursal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SucursalMapper {
    Sucursal buscarPorId(@Param("idSucursal") int idSucursal);
}
```

El nombre completo de esta interfaz debe ser igual al `namespace` de su XML.

## 8. Mapper XML: resultMap, SQL compartido y CRUD

El XML mapper es donde MyBatis enlaza métodos Java con instrucciones de base de datos. Una forma organizada para un mapper de entidad incluye:

1. `<mapper namespace="...">` que señala la interfaz Java.
2. Un `<resultMap>` que relaciona columnas y propiedades.
3. Un fragmento `<sql id="...">` si varias consultas comparten SELECT y columnas.
4. Statements `<select>`, `<insert>`, `<update>` y `<delete>` para los métodos CRUD.

### Ejemplo de SELECT compartido

```xml
<sql id="sqlSucursal">
    SELECT s.id_sucursal, s.id_pais, s.nombre, s.hora
    FROM sucursales s
</sql>

<select id="listarSucursales" resultMap="sucursalMap">
    <include refid="sqlSucursal"/>
    ORDER BY s.id_sucursal
</select>

<select id="buscarPorId" resultMap="sucursalMap">
    <include refid="sqlSucursal"/>
    WHERE s.id_sucursal = #{idSucursal}
</select>
```

El fragmento evita volver a escribir las mismas columnas y el mismo FROM. Cada consulta mantiene su condición y orden propios. El fragmento por sí solo no crea ni actualiza filas.

### Ejemplo de INSERT

```xml
<insert id="crearSucursal" parameterType="com.ejemplo.sucursal.dto.CrearSucursalDTO">
    INSERT INTO sucursales (
        id_pais
        , nombre
        , hora
    ) VALUES (
        #{idPais}
        , #{nombre}
        , #{hora}
    )
</insert>
```

El SQL deja visibles los campos que se guardan. Cada `#{...}` corresponde a una propiedad del DTO recibido. El ID generado por la base de datos normalmente se omite de la lista si la columna es SERIAL/IDENTITY.

### Ejemplo de UPDATE

```xml
<update id="actualizarSucursal">
    UPDATE sucursales SET
        id_pais = #{idPais}
        , nombre = #{nombre}
        , hora = #{hora}
    WHERE id_sucursal = #{idSucursal}
</update>
```

La condición WHERE debe identificar la fila a actualizar. Revisar que use el parámetro correcto y que la operación no modifique todas las filas por accidente.

### Ejemplo de PATCH

```xml
<update id="actualizarParcialmente">
    UPDATE sucursales
    <set>
        <if test="nombre != null">nombre = #{nombre},</if>
        <if test="hora != null">hora = #{hora},</if>
    </set>
    WHERE id_sucursal = #{idSucursal}
</update>
```

El PATCH debe cambiar solo los campos enviados. La validación debe rechazar un body vacío para evitar un UPDATE sin cambios.

### Ejemplo de resultMap

```xml
<resultMap id="sucursalMap" type="com.ejemplo.sucursal.model.Sucursal">
    <id property="idSucursal" column="id_sucursal"/>
    <result property="idPais" column="id_pais"/>
    <result property="nombre" column="nombre"/>
    <result property="hora" column="hora"/>
</resultMap>
```

- `type` es la clase Java a rellenar.
- `column` es la columna que existe en la base.
- `property` es una propiedad existente en la clase Java.
- `<id>` marca la propiedad que identifica el registro.
- Por ejemplo, `id_sucursal = 7` llena `idSucursal = 7`. MyBatis crea el objeto y asigna los valores; no cambia los nombres ni la estructura de PostgreSQL.
- El SELECT debe devolver las columnas que enumera el `resultMap`.

## 9. Cómo se conectan GET, POST, UPDATE y DELETE

### GET

```text
Ruta HTTP → controller → service → método del mapper
→ SELECT XML → filas PostgreSQL → resultMap → DTO/respuesta JSON
```

El getter como `getNombre()` solo lee un dato que ya está en un objeto Java. El mapper y el SQL son los que consultan PostgreSQL.

### POST

```text
JSON → DTO de entrada → controller → service/validación
→ método del mapper → INSERT XML → PostgreSQL
```

Después de insertar, recuperar el registro creado con su ID generado si la respuesta debe incluirlo.

### PUT y PATCH

```text
JSON → DTO → controller → service/validación
→ UPDATE XML → filas afectadas → respuesta o 404
```

Para PUT validar todos los campos requeridos. Para PATCH distinguir un campo ausente de un valor presente y actualizar únicamente lo que se envió.

### DELETE

```text
Ruta con identificador → controller → service → DELETE XML
→ cantidad de filas eliminadas → 204 o 404
```

Un método DELETE que no encuentra una fila no debe presentarse como si hubiera eliminado algo.

## 10. Enums

- Usar enum para un conjunto realmente cerrado de valores. Ejemplos: estados con opciones fijas o monedas permitidas.
- Mantener como texto libre los campos cuyo contenido puede cambiar, como país o puesto, si así lo define el negocio.
- Un enum Java define las opciones. Un TypeHandler MyBatis convierte el enum a la representación almacenada y viceversa. El handler es una clase, no otro enum.
- Definir y documentar qué se almacena: nombre como `PEN`, código como `PEN`, o número como `6`. No asumir que son equivalentes.
- Antes de agregar un enum a una API, revisar cómo se serializa a JSON y qué acepta el cliente.

## 11. Pruebas

- No añadir ni ejecutar pruebas por rutina si no fueron solicitadas o no hacen falta para validar un cambio de forma responsable.
- Cuando se pidan pruebas de controllers CRUD, mantenerlas breves y centradas en respuestas visibles:
  - El mapper devuelve un registro → GET responde 200.
  - El mapper devuelve null → GET responde 404.
- Usar las herramientas de test que ya estén en el proyecto, como MockMvc y un mapper simulado para una prueba web.
- No crear decenas de tests de una sola línea para repetir la misma comprobación.
- Decir qué se probó exactamente. Un test MVC con mapper simulado no demuestra que PostgreSQL ejecutó el SQL.
- Respetar la carpeta de test ya configurada. Para un proyecto nuevo, no crear carpetas de test si no se solicitaron.

## 12. Base de datos y Docker

- Conservar el esquema existente: nombres, tipos, claves y relaciones.
- Para scripts iniciales de proyecto, evitar operaciones destructivas como `DROP TABLE` salvo petición explícita y entendida.
- Usar `CREATE TABLE IF NOT EXISTS` cuando el script deba crear tablas que falten sin borrar datos.
- Recordar que Docker puede conservar la base de datos en un volumen y que los scripts de inicialización normalmente no se vuelven a ejecutar sobre un volumen ya inicializado.
- No borrar volúmenes, bases ni datos como parte de una solución rutinaria. No ejecutar `docker compose down -v` sin autorización explícita.
- Cuando se cambie el backend en un proyecto Compose, explicar si hace falta reconstruir la imagen con `docker compose up -d --build`.
- Distinguir contenedor iniciado de endpoint funcional. Verificar el estado del servicio y, cuando se solicite una comprobación real, consultar el endpoint/API o revisar logs.

## 13. Secuencia de trabajo para una IA

Antes de editar:

1. Leer la petición completa y conservar las preferencias que sigan vigentes.
2. Identificar el proyecto y alcance exactos a partir de la conversación y los archivos actuales. No asumir que todos los proyectos vecinos forman parte del trabajo.
3. Leer el código directamente relacionado: ruta/controller, servicio, DTO/modelo, mapper, XML, configuración y esquema cuando apliquen.
4. Seguir una operación completa y comprobar qué archivo tiene cada responsabilidad.
5. Elegir el cambio más pequeño que resuelva el problema sin romper la estructura acordada.

Al implementar:

1. Mantener los nombres públicos y el esquema salvo que la petición los cambie.
2. Mostrar con claridad qué datos entran, qué columnas se consultan o modifican y qué resultado sale.
3. Reutilizar la estructura existente antes de crear archivos o abstracciones.
4. No dejar métodos sin implementación, XML sin método correspondiente ni referencias a clases eliminadas.
5. No tocar documentación, contenedores, datos o proyectos ajenos al alcance.

Al finalizar:

1. Comprobar compilación si se cambió Java.
2. Si se cambió XML/MyBatis, comprobar que los recursos cargan y que `namespace`, `id`, parámetros, fragmentos y `resultMap` coinciden.
3. Ejecutar solo la verificación pertinente y autorizada.
4. Informar los archivos cambiados, el motivo y el resultado de la comprobación.
5. Aclarar si la comprobación fue de compilación, un test simulado o acceso real a la base de datos.

## 14. Decisiones para mantener el código sencillo

| Si aparece esta situación | Aplicar esta regla |
|---|---|
| Se propone un archivo nuevo | Explicar qué responsabilidad concreta tendrá y por qué no cabe claramente en uno existente. |
| Un campo de base de datos usa snake_case | Mantenerlo así y mapearlo a camelCase en Java. |
| Varias consultas repiten SELECT y columnas | Crear un fragmento XML `<sql>` y reutilizarlo con `<include>`. |
| Una propiedad no coincide en nombre entre SQL y Java | Definir el mapeo explícito en `resultMap`. |
| Se quiere añadir un enum | Comprobar primero que los valores sean realmente cerrados y estables. |
| Se recibe un dato del cliente | Validarlo y representar el contrato con DTO claro. |
| Un error se puede manejar con el handler existente | Reutilizarlo en lugar de añadir jerarquías nuevas. |
| La solución requiere borrar una tabla o volumen | Detenerse: no hacerlo sin autorización explícita. |
| No se sabe cómo funciona una pieza | Seguir la llamada real desde la ruta al SQL y explicar ese recorrido con código del proyecto. |

La meta de esta guía es producir código funcional que se pueda entender a primera lectura, con las decisiones y datos importantes visibles en sus archivos correctos.

