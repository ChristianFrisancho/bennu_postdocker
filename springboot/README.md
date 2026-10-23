# API de trabajadores

Java 17, Spring Boot, MyBatis y PostgreSQL 16.

`aplicacion.java` inicia el servidor, `trabajadores.java` contiene el mapper MyBatis y `crud.java` recibe las solicitudes HTTP y conserva los prints originales del CRUD.

## Ejecutar con Docker

Primero genera el JAR desde `C:\Users\Christian\Desktop\Bennu_docker\springboot`:

```powershell
.\mvnw.cmd package
```

Luego, desde `C:\Users\Christian\Desktop\Bennu_docker`:

```powershell
docker compose up -d
```

Compose usa una imagen de Java para ejecutar el JAR. La API queda en `http://localhost:8080/api/trabajadores`.

La aplicacion usa `jdbc:postgresql://db_trabajadores:5432/trabajadores` dentro de Docker. Desde Windows, PostgreSQL esta disponible en `localhost:5433`, base `trabajadores`, usuario `bennu`, clave `admin`.

Compose reutiliza la base y las tablas existentes del volumen `datos_trabajadores`.

Si el contenedor `bennu_postgres` fue creado manualmente, hay que detenerlo y renombrarlo una sola vez antes del primer `docker compose up`, para que Compose pueda administrar el nuevo contenedor con el mismo volumen:

```powershell
docker stop bennu_postgres
docker rename bennu_postgres bennu_postgres_anterior
docker compose up -d
```

Para detener los servicios conservando los datos:

```powershell
docker compose down
```

## API REST y Postman

La ruta se declara directamente en `src/main/java/bennu/crud/crud.java` mediante `@RequestMapping("/api/trabajadores")`.

Con la aplicacion en ejecucion, crea una solicitud GET en Postman con la URL `http://localhost:8080/api/trabajadores` para consultar los trabajadores. Esta URL corresponde al servidor HTTP; no es una carpeta del proyecto.

| Metodo | Ruta | Resultado |
| --- | --- | --- |
| GET | `/api/trabajadores` | Lista todos los trabajadores, 200 |
| GET | `/api/trabajadores?nombre=Ana` | Busca por nombres sin distinguir mayusculas, 200 |
| GET | `/api/trabajadores/{dni}` | Consulta por DNI, 200 o 404 |
| POST | `/api/trabajadores` | Crea un trabajador, 201 |
| PUT | `/api/trabajadores/{dni}` | Actualiza todos los campos, 200 o 404 |
| PATCH | `/api/trabajadores/{dni}/puesto` | Actualiza solo el puesto, 200 o 404 |
| DELETE | `/api/trabajadores/{dni}` | Elimina por DNI, 204 o 404 |

Cuerpo JSON para POST y PUT:

```json
{
  "idSucursal": 1,
  "dni": "99887766-5",
  "nombres": "Lucas",
  "apellidos": "Silva",
  "puesto": "Analista"
}
```

En Postman, selecciona Body, raw y JSON para enviar el cuerpo. `idSucursal` debe corresponder a una sucursal existente. En PUT, el DNI de la ruta identifica al trabajador actual y el DNI del cuerpo es el valor que se guardara. Las respuestas incluyen `id_trabajador`, `id_sucursal`, `dni`, `nombres`, `apellidos` y `puesto`.

Cuerpo JSON para PATCH:

```json
{
  "puesto": "Desarrollador"
}
```

Los campos obligatorios, longitudes e ID de sucursal positivo se validan con HTTP 400. Un DNI duplicado, una sucursal inexistente o una referencia que impida eliminar producen HTTP 409. Los errores se devuelven como JSON Problem Details.

## Ejecutar desde Java

Con PostgreSQL disponible en `localhost:5433`, desde `springboot`:

```powershell
.\mvnw.cmd spring-boot:run
```

La conexion se puede configurar mediante `DB_URL`, `DB_USER` y `DB_PASSWORD`. El puerto HTTP se configura mediante `PORT` y por defecto es 8080.

