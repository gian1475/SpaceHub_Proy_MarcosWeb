-- Ejecutar despues del esquema y los datos iniciales.
USE spacehub_db;
SHOW TABLES;
SELECT 'roles' AS tabla, COUNT(*) AS cantidad FROM roles
UNION ALL SELECT 'usuarios', COUNT(*) FROM usuarios
UNION ALL SELECT 'categorias', COUNT(*) FROM categorias
UNION ALL SELECT 'servicios', COUNT(*) FROM servicios
UNION ALL SELECT 'servicio_incluidos', COUNT(*) FROM servicio_incluidos
UNION ALL SELECT 'servicio_imagenes', COUNT(*) FROM servicio_imagenes
UNION ALL SELECT 'reservas', COUNT(*) FROM reservas;

SELECT r.id, u.correo, s.nombre, c.nombre AS categoria,
       r.fecha, r.hora_inicio, r.hora_fin, r.total, r.estado
FROM reservas r
JOIN usuarios u ON u.id = r.usuario_id
JOIN servicios s ON s.id = r.servicio_id
JOIN categorias c ON c.id = s.categoria_id
ORDER BY r.id;

SELECT TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'spacehub_db' AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, COLUMN_NAME;
