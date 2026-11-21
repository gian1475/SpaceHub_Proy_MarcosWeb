# SpaceHub

Aplicación académica para consultar espacios y gestionar reservas con Spring Boot, Thymeleaf y MySQL.

## Ejecutar

Abre esta carpeta, la que contiene `pom.xml`, en VS Code. Con MySQL encendido y los datos importados, ejecuta:

```powershell
.\iniciar-spacehub.ps1
```

Introduce las credenciales de MySQL y abre [SpaceHub](http://localhost:8080) cuando termine el arranque. Mantén la terminal abierta.

La [guía de instalación y demostración](database/README.md) explica la carga inicial, las cuentas de ejemplo, los CRUD, el DTO y las consultas en cascada.

## Verificar

```powershell
.\mvnw.cmd verify
node --test src/test/js/interfaz.test.cjs cliente-api/server.test.cjs cliente-api/interfaz.test.cjs
```

Las pruebas Java usan H2 en memoria y no modifican MySQL. Las pruebas JavaScript requieren Node.js 18 o superior. Los archivos de compilación se generan en `target/`.

## Alcance

ADMIN puede gestionar roles y cuentas activas desde `/admin/usuarios`. La API requiere JWT: `POST /api/auth/login` recibe correo y contraseña y entrega un token para enviar como `Authorization: Bearer ...`. Incluye `GET /api/servicios` y `GET`/`POST /api/reservas`; el listado de reservas pertenece al usuario autenticado. El cliente Node solicita credenciales y usa ese mismo contrato. Consulta los ejemplos y configuración de la clave en [database/README.md](database/README.md).

La página **Consulta API** está disponible en `/consulta-api`. Para demostrar consumo fuera de Spring Boot, ejecuta `node cliente-api/server.cjs` en otra terminal y abre la dirección que indique la terminal con Spring iniciado. La API protegida con JWT empieza en `/api/v1/categorias` y `/api/v1/espacios`.

Incluye catálogo, registro, sesiones, permisos administrativos, CRUD de categorías, espacios y reservas, listados con DTO y filtros Categoría → Espacio en dos páginas. Incluye bandeja de mensajes de contacto para administradores y gestión de espacios y solicitudes para propietarios. Los pagos y el envío de correos no están implementados.
