# SpaceHub Guía del avance 3 y ejecución

Esta guía explica lo que implementamos y cómo ejecutar SpaceHub desde VS Code, PowerShell y MySQL Workbench en Windows.

## 1. Qué hicimos paso a paso

### Paso 1. Diseñar la base de datos

Creamos `spacehub_db` con siete tablas y seis claves foráneas:

| Tabla | Función y relaciones |
| --- | --- |
| `roles` | ADMIN, CLIENTE y PROPIETARIO; un rol tiene muchos usuarios. |
| `usuarios` | Cuentas registradas; cada usuario pertenece a un rol. |
| `categorias` | Clasificación de los espacios; una categoría tiene muchos servicios. |
| `servicios` | Espacios de alquiler; cada espacio pertenece a una categoría. |
| `servicio_incluidos` | Comodidades de cada espacio, como WiFi. |
| `servicio_imagenes` | Fotografías por URL, asociadas a cada espacio. |
| `reservas` | Solicitudes relacionadas con un usuario y un espacio. |

Definimos claves primarias autoincrementales, restricciones de integridad, correos y nombres de categoría únicos e importes `DECIMAL(10,2)`.

### Paso 2. Conectar Spring Boot con MySQL

Agregamos Spring Data JPA, Hibernate, MySQL Connector/J y Validation. Convertimos los modelos en entidades y creamos cinco repositorios.

La conexión usa variables de entorno en `src/main/resources/application.properties`. Hibernate tiene `ddl-auto=validate`: verifica el esquema sin crearlo ni modificarlo. Los scripts SQL se ejecutan manualmente.

### Paso 3. Reemplazar los datos en memoria

Sustituimos listas y contadores por repositorios JPA. Catálogo, filtros, destacados, usuarios, reservas y métricas ahora usan MySQL. Los datos permanecen después de reiniciar; el arranque no vuelve a insertar los ejemplos.

### Paso 4. Completar el CRUD de categorías

Implementamos registro, listado, edición y eliminación. Validamos campos, longitudes y nombres duplicados. No se permite eliminar categorías con espacios asociados.

### Paso 5. Completar el CRUD de espacios

Conectamos Nuevo Espacio al backend y agregamos listado, edición y eliminación. El formulario permite elegir una categoría existente, indicar precio y capacidad y gestionar fotos y comodidades.

- Fotos opcionales: una URL `http` o `https` por línea.
- Comodidades opcionales: una por línea, por ejemplo `bi-wifi|WiFi`.
- Un espacio con reservas no puede eliminarse.
- Los espacios sin fotos muestran un mensaje y no rompen el catálogo.

### Paso 6. Implementar usuarios y sesión

Agregamos validaciones de registro, login y cierre de sesión. Solo ADMIN accede a `/administracion` y `/admin/**`. Cada cliente ve únicamente sus reservas y las solicitudes se vinculan al usuario conectado.

Las cuentas nuevas guardan la contraseña con hash BCrypt. Las cuentas antiguas en texto plano se convierten después de un login correcto, sin cambiar la contraseña de acceso. Los formularios POST usan protección CSRF y la sesión vence tras 30 minutos de inactividad.

PROPIETARIO conserva el acceso ordinario de cliente; su panel propio no está implementado. Se usa el módulo de criptografía de Spring Security; la integración completa de Spring Security y JWT queda para la entrega 4.

### Paso 7. Completar el CRUD de reservas

Agregamos listado administrativo, edición de fecha y horario, aprobación, rechazo y eliminación con confirmación.

- Las nuevas solicitudes deben comenzar en el futuro, según America/Lima.
- La hora final debe ser posterior a la inicial; no se admiten intervalos que crucen medianoche.
- Pendiente y Aprobada bloquean el horario; Rechazada lo libera.
- Se permiten intervalos consecutivos: 10:00–11:00 y 11:00–12:00.
- La comprobación de cruces usa transacciones y bloqueo del espacio para coordinar solicitudes simultáneas.
- Los importes se calculan con `BigDecimal` e incluyen minutos. A S/ 45.50 por hora, 90 minutos cuestan S/ 68.25.
- Cambiar fecha u horas recalcula el total con la tarifa actual; cambiar solo el estado conserva el importe.
- El indicador Total solicitado representa reservas no rechazadas, no pagos cobrados.

### Paso 8. Realizar la verificación final

Comprobamos persistencia, CRUD, sesiones, permisos, validaciones y solicitudes simultáneas. Corregimos la carga del rol al verificar la sesión, las tildes, la alineación de botones y el límite de importe de las reservas.

También validamos contacto. **El formulario de contacto es demostrativo: no envía ni guarda mensajes.**

La última ejecución de `verify` registró **13 pruebas sin fallos ni errores** y generó el JAR. Las pruebas automáticas usan H2 aislado; los flujos con MySQL se comprobaron durante las pruebas locales con el usuario.

Los archivos `FASE_2.md` a `FASE_8.md` documentan el avance histórico. Para el funcionamiento actual, utiliza esta guía.

## 2. Requisitos

- JDK 17 o superior. El proyecto está configurado para Java 17 y también se probó con JDK 21.
- MySQL 8.0.16 o posterior, para aplicar las restricciones CHECK.
- MySQL Workbench para importar y consultar la base.
- VS Code y una terminal PowerShell en la carpeta del proyecto.
- Internet durante la primera ejecución para descargar Maven y dependencias.

No necesitas instalar Maven por separado: el proyecto incluye `mvnw.cmd`.

Comprueba Java:

```powershell
java -version
```

Si falta Java o `JAVA_HOME`, configúralo según la carpeta real de tu JDK. Ejemplo:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

## 3. Primera instalación de la base de datos

Este apartado se hace una sola vez en una instalación nueva. **Si ya importaste la base, pasa al apartado 4.**

1. Inicia MySQL como se explica en el apartado 4.
2. Abre Workbench y entra a tu conexión local.
3. Comprueba si existe `spacehub_db`. Si contiene datos propios, revisa su estructura antes de importar; estos scripts no son migraciones.
4. Abre y ejecuta completo [01_schema.sql](01_schema.sql): crea la base y las siete tablas.
5. Ejecuta [02_seed.sql](02_seed.sql) una sola vez sobre las tablas vacías: carga los ejemplos. Si falla, ejecuta `ROLLBACK;` en esa misma conexión y revisa el error antes de continuar.
6. Ejecuta [03_verify.sql](03_verify.sql): muestra tablas, cantidades y relaciones.

En Workbench puedes ejecutar el archivo completo con Ctrl + Shift + Enter, sin una selección parcial.

| Tabla | Cantidad inicial |
| --- | ---: |
| roles | 3 |
| usuarios | 4 |
| categorias | 4 |
| servicios | 6 |
| servicio_incluidos | 30 |
| servicio_imagenes | 18 |
| reservas | 3 |

Deben existir siete tablas y seis claves foráneas. Las cantidades cambian si ya hiciste pruebas. El diagrama y los campos están en [modelo-relacional.md](modelo-relacional.md).

**No vuelvas a ejecutar el seed para encender la web.** Puede provocar errores de IDs duplicados. `IF NOT EXISTS` del esquema tampoco actualiza tablas preexistentes.

## 4. Encender MySQL

En este equipo el servicio se llama `MySQL95`. Abre PowerShell **como administrador** y ejecuta:

```powershell
Get-Service -Name MySQL95
Start-Service -Name MySQL95
Get-Service -Name MySQL95
```

Debe aparecer `Running`. Si ya estaba encendido, no necesitas iniciarlo otra vez.

En otro equipo el nombre puede variar. Búscalo con:

```powershell
Get-Service -Name '*mysql*'
```

Utiliza el nombre correspondiente a ese equipo. Los permisos de administrador son para iniciar el servicio; no necesitas ejecutar VS Code como administrador.

## 5. Ejecutar SpaceHub desde VS Code

### 5.1 Abrir la carpeta correcta

Abre la carpeta del proyecto y selecciona Terminal → New Terminal. Debes estar donde aparecen `pom.xml`, `mvnw.cmd` y `src`.

Compruébalo con:

```powershell
Get-Location
Test-Path .\pom.xml
Test-Path .\mvnw.cmd
```

Los dos últimos comandos deben devolver `True`.

### 5.2 Configurar la conexión e iniciar

Copia este bloque completo **sin agregar comillas alrededor del bloque**:

```powershell
$env:SPACEHUB_DB_USER = 'root'
$credencialSpaceHub = Get-Credential -UserName $env:SPACEHUB_DB_USER -Message 'Acceso a MySQL'
$env:SPACEHUB_DB_PASSWORD = $credencialSpaceHub.GetNetworkCredential().Password
.\mvnw.cmd spring-boot:run
```

Introduce la contraseña de MySQL que utilizas en Workbench. **Las credenciales de MySQL son distintas de las de la web:** aquí no debes introducir `admin@spacehub.pe` ni `admin123`.

Si usas otro usuario MySQL, cambia `root`. La contraseña se mantiene en el entorno de esta terminal y del proceso Java, sin guardarse en `application.properties`.

La dirección predeterminada de la base es `jdbc:mysql://localhost:3306/spacehub_db`. Si tu conexión usa otro puerto, configúralo antes de arrancar. Ejemplo para 3307:

```powershell
$env:SPACEHUB_DB_URL = 'jdbc:mysql://localhost:3307/spacehub_db'
```

### 5.3 Esperar el arranque y abrir la web

Espera mensajes similares a:

```text
Initialized JPA EntityManagerFactory
Tomcat started on port 8080
Started MarcosDesarrolloWebApplication
```

Abre **http://localhost:8080/** y deja abierta la terminal de Java. Desde otra terminal también puedes abrirla con:

```powershell
Start-Process 'http://localhost:8080/'
```

## 6. Cuentas de demostración

Estas cuentas son para pruebas locales y funcionan si sus datos están cargados y no cambiaste las contraseñas.

| Cuenta | Correo | Contraseña |
| --- | --- | --- |
| Administrador del seed | `admin@spacehub.pe` | `admin123` |
| Cliente creado durante las pruebas locales | `prueba.fase3@example.com` | `PruebaFase3!2026` |
| Cliente del seed | `gerardo@correo.com` | `demo12345` |

`prueba.fase3@example.com` se creó manualmente durante el desarrollo: **no forma parte del seed**. En una instalación nueva usa el cliente del seed o registra una cuenta en `/registro`.

Al entrar correctamente, una contraseña antigua puede convertirse a hash en MySQL. Sigue usando la contraseña original para iniciar sesión, nunca el hash.

## 7. Rutas y comprobación funcional

| Dirección | Función y acceso |
| --- | --- |
| http://localhost:8080/ | Inicio público |
| http://localhost:8080/servicios | Catálogo público |
| http://localhost:8080/registro | Crear cuenta |
| http://localhost:8080/login | Iniciar sesión |
| http://localhost:8080/reservas | Historial de la cuenta conectada |
| http://localhost:8080/administracion | Panel ADMIN |
| http://localhost:8080/admin/categorias | CRUD de categorías, solo ADMIN |
| http://localhost:8080/admin/servicios | CRUD de espacios, solo ADMIN |
| http://localhost:8080/admin/reservas | CRUD de reservas, solo ADMIN |
| http://localhost:8080/contacto | Formulario demostrativo con validaciones |

Prueba recomendada:

1. Entra como administrador y comprueba los tres CRUD.
2. Registra una categoría y un espacio de prueba. Edítalos y verifica los cambios en Workbench.
3. Pulsa Salir e inicia sesión como cliente.
4. Solicita una reserva futura desde el detalle de un espacio Disponible.
5. Comprueba que aparece en tu historial. Intenta reservar el mismo espacio con un horario cruzado: debe bloquearse.
6. Entra como administrador, aprueba o rechaza la solicitud y comprueba el cambio desde el cliente.
7. Reinicia Spring Boot y verifica que los datos permanecen.

Consultas de comprobación en Workbench:

```sql
USE spacehub_db;
SELECT id, nombre FROM categorias ORDER BY id;
SELECT id, nombre, precio_por_hora FROM servicios ORDER BY id;
SELECT r.id, u.correo, s.nombre, r.fecha, r.hora_inicio,
       r.hora_fin, r.total, r.estado
FROM reservas r
JOIN usuarios u ON u.id = r.usuario_id
JOIN servicios s ON s.id = r.servicio_id
ORDER BY r.id DESC;
```

Utiliza registros de prueba para las eliminaciones. Las categorías con espacios y los espacios con reservas están protegidos por sus dependencias.

## 8. Detener y volver a ejecutar

Para detener Spring Boot, pulsa Ctrl + C en la terminal de Java y espera a recuperar el indicador de PowerShell.

En la misma terminal, las variables de conexión siguen disponibles y basta con:

```powershell
.\mvnw.cmd spring-boot:run
```

Si cierras la terminal o VS Code, repite el bloque de credenciales del apartado 5.2. Si reinicias Windows, comprueba también que MySQL esté iniciado.

Al terminar puedes limpiar las credenciales de esa terminal:

```powershell
Remove-Item Env:SPACEHUB_DB_PASSWORD -ErrorAction SilentlyContinue
Remove-Variable credencialSpaceHub -ErrorAction SilentlyContinue
```

Detener Spring Boot no detiene MySQL. Si necesitas apagar el servicio y no lo usan otras aplicaciones, ejecuta `Stop-Service -Name MySQL95` desde PowerShell como administrador.

## 9. Ejecutar pruebas y generar el JAR

Desde la carpeta que contiene `pom.xml`:

```powershell
.\mvnw.cmd test
```

Para ejecutar las pruebas y generar el paquete:

```powershell
.\mvnw.cmd verify
```

Las pruebas usan H2 en memoria: no requieren credenciales de MySQL ni modifican tu base local. Los resultados quedan en `target/surefire-reports/`.

Con `BUILD SUCCESS` se genera `target/spacehub-0.0.1-SNAPSHOT.jar`. Para ejecutarlo, detén primero la instancia anterior y configura las variables de MySQL del apartado 5.2. Luego, en lugar de ejecutar Maven, usa:

```powershell
java -jar .\target\spacehub-0.0.1-SNAPSHOT.jar
```

## 10. Solución de errores frecuentes

| Error o síntoma | Qué revisar |
| --- | --- |
| No se puede iniciar `MySQL95` por permisos | Abre PowerShell como administrador para iniciar el servicio. |
| `Access denied for user` | Revisa usuario y contraseña de MySQL; repite el bloque de credenciales. |
| `Communications link failure` | Comprueba servicio MySQL, host y puerto. |
| `Unknown database` o `Schema-validation` | Revisa la importación del esquema y que la conexión apunte a `spacehub_db`. No borres tablas sin revisar el error. |
| `Port 8080 already in use` | Detén la instancia anterior con Ctrl + C. No inicies dos copias en el mismo puerto. |
| `ERR_CONNECTION_REFUSED` | Espera a que Tomcat arranque y comprueba que Java siga ejecutándose. Recarga o abre el navegador externo. |
| Acceso 403 | Puede ser falta de permisos o un formulario con token vencido. Usa el rol correcto y recarga el formulario. |
| Redirección al login | La ruta requiere sesión o la sesión venció. Inicia sesión nuevamente. |
| IDs duplicados al cargar el seed | Ya se importaron los ejemplos. No repitas el seed sobre la base existente. |
| No aparecen reservas anteriores | `/reservas` muestra solo las propias. ADMIN puede ver todas desde `/admin/reservas`. |
| Cambios visuales antiguos | Recarga con Ctrl + F5; si cambió Java, reinicia la aplicación. |
| Maven no puede usar su repositorio local | Revisa permisos de `.m2` o usa la alternativa indicada debajo. |

Alternativa para indicar explícitamente la caché Maven del usuario actual:

```powershell
.\mvnw.cmd "-Dmaven.repo.local=$env:USERPROFILE/.m2/repository" spring-boot:run
```

Si persiste un error, revisa el primer mensaje `ERROR` y la sección `Caused by` de la terminal. No vuelvas a importar toda la base como primer intento.

## 11. Alcance y entrega

El avance incluye MySQL, siete tablas relacionadas, JPA/Hibernate, tres CRUD, validaciones, sesiones y pruebas. No incluye pagos, envío de consultas de contacto, notificaciones, recuperación de contraseña ni panel propio de propietarios.

La entrega debe acompañarse del código, los tres scripts SQL, el informe actualizado, el diagrama EER y las capturas de CRUD y validaciones. La preparación de esos documentos y la actualización del repositorio se comprueban por separado: generar el JAR no publica cambios en GitHub.
