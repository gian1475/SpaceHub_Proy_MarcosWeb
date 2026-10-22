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
node --test src/test/js/interfaz.test.cjs
```

Las pruebas Java usan H2 en memoria y no modifican MySQL. Las pruebas JavaScript requieren Node.js 18 o superior. Los archivos de compilación se generan en `target/`.

## Alcance

Incluye catálogo, registro, sesiones, permisos administrativos, CRUD de categorías, espacios y reservas, listados con DTO y filtros Categoría → Espacio en dos páginas. Los pagos, el envío de mensajes y el panel de propietarios todavía no están implementados.
