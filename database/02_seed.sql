-- SOLO para una base recien creada y vacia. Ejecutar una vez.

-- IDs explicitos: no ejecutar sobre datos propios. Sin INSERT IGNORE ni reemplazos.

SET NAMES utf8mb4;

USE spacehub_db;

START TRANSACTION;

INSERT INTO roles (id, nombre) VALUES
(1, 'ADMIN'),
(2, 'CLIENTE'),
(3, 'PROPIETARIO');


INSERT INTO categorias (id, nombre, icono, descripcion) VALUES
(1, 'Coworking', 'bi-laptop', 'Mesas compartidas y oficinas privadas para trabajar cómodo.'),
(2, 'Salones', 'bi-building', 'Auditorios amplios para conferencias, talleres y capacitaciones.'),
(3, 'Rooftops', 'bi-sun', 'Terrazas al aire libre con vista panorámica para eventos sociales.'),
(4, 'Estudios', 'bi-camera', 'Sets profesionales con iluminación para sesiones de fotos y video.');


INSERT INTO servicios (id, nombre, categoria_id, distrito, direccion, referencia, precio_por_hora, capacidad, estado, descripcion_corta, descripcion) VALUES
(1, 'Coworking Loft San Isidro', 1, 'San Isidro', 'Av. Las Camelias 450, San Isidro - Lima', 'San Isidro, a 1 cuadra de la estación Canaval y Moreyra del Metropolitano.', 45.0, 15, 'Disponible', 'Luz natural, escritorios ergonómicos, café ilimitado y zona chill out para trabajar cómodo.', 'Ambiente cálido con luz natural, mesas amplias de madera, café de especialidad ilimitado y zona chill out para trabajar cómodo y productivo. Espacio ideal para freelancers, programadores y equipos que buscan concentrarse sin interrupciones.'),
(2, 'Rooftop Sunset Miraflores', 3, 'Miraflores', 'Av. Larco 1230, Miraflores - Lima', 'Miraflores, a 2 cuadras del Parque del Amor y la Costa Verde.', 190.0, 70, 'Disponible', 'Terraza exclusiva con vista al mar. Equipado con sillones lounge, barra y sistema de sonido.', 'Disfruta de atardeceres espectaculares frente al océano en nuestra terraza privada en Miraflores. Cuenta con iluminación ambiental cálida, cómodos sillones lounge, barra para coctelería y equipo de sonido envolvente.'),
(3, 'Auditorio Magna Surco', 2, 'Surco', 'Av. Primavera 890, Surco - Lima', 'Surco, cerca al Centro Comercial Chacarilla y Av. Encalada.', 220.0, 120, 'Disponible', 'Auditorio acústico con butacas cómodas, podio para expositores y proyector láser 4K.', 'Auditorio acústico moderno diseñado para ponencias de alto impacto, lanzamientos de marca y capacitaciones corporativas. Equipado con pantallas gigantes, micrófonos inalámbricos y climatización centralizada.'),
(4, 'Studio Click Barranco', 4, 'Barranco', 'Av. Pedro de Osma 210, Barranco - Lima', 'Barranco, a pocos pasos de la Plaza Principal y el Puente de los Suspiros.', 80.0, 20, 'Disponible', 'Estudio profesional para sesiones de fotografía y video. Incluye ciclorama blanco e iluminación.', 'Estudio fotográfico completamente equipado con ciclorama sin fin de 5 metros de ancho, luces continuas y flashes de alta potencia. Espacio versátil ideal para producciones de moda, anuncios comerciales y contenido digital.'),
(5, 'Sala Boardroom Empresarial', 1, 'San Isidro', 'Calle Dean Valdivia 180, San Isidro - Lima', 'San Isidro, pleno centro financiero a espaldas de Av. Rivera Navarrete.', 60.0, 12, 'Disponible', 'Sala de directorio privada para 12 personas con pantalla 65'''' para videollamadas Zoom.', 'Sala ejecutivo-corporativa elegante aislada acústicamente, idónea para reuniones de junta, presentaciones a clientes y videoconferencias internacionales. Incluye estación de café gourmet y asistencia ejecutiva.'),
(6, 'Terraza Bohemia Barranco', 3, 'Barranco', 'Av. Grau 405, Barranco - Lima', 'Barranco, con vista privilegiada al boulevard cultural y turístico.', 160.0, 50, 'Disponible', 'Ambiente bohemio al aire libre con plantas, sillones cómodos y vista al boulevard de Barranco.', 'Un oasis urbano en el corazón de Barranco. Terraza rodeada de vegetación nativa, ambientación vintage y luces de guirnalda que crean una atmósfera acogedora e inolvidable para eventos privados y corporativos.');


INSERT INTO servicio_incluidos (id, servicio_id, icono, texto) VALUES
(1, 1, 'bi-wifi', 'WiFi 500 Mbps'),
(2, 1, 'bi-cup-hot', 'Café ilimitado'),
(3, 1, 'bi-easel', 'Pizarra acrílica'),
(4, 1, 'bi-snow', 'Aire acondicionado'),
(5, 1, 'bi-shield-check', 'Seguridad 24h'),
(6, 2, 'bi-sun', 'Vista al mar'),
(7, 2, 'bi-music-note-beamed', 'Sistema de sonido'),
(8, 2, 'bi-cup-straw', 'Barra equipada'),
(9, 2, 'bi-shield-check', 'Seguridad 24h'),
(10, 2, 'bi-wifi', 'WiFi alta velocidad'),
(11, 3, 'bi-display', 'Proyector láser 4K'),
(12, 3, 'bi-mic', 'Podio y micrófonos'),
(13, 3, 'bi-wifi', 'WiFi alta velocidad'),
(14, 3, 'bi-snow', 'Aire acondicionado'),
(15, 3, 'bi-shield-check', 'Seguridad 24h'),
(16, 4, 'bi-camera', 'Ciclorama blanco'),
(17, 4, 'bi-lightbulb', 'Iluminación continua'),
(18, 4, 'bi-wifi', 'WiFi 500 Mbps'),
(19, 4, 'bi-snow', 'Aire acondicionado'),
(20, 4, 'bi-easel', 'Camerino y maquillaje'),
(21, 5, 'bi-display', 'Pantalla 65'''' Zoom'),
(22, 5, 'bi-wifi', 'WiFi 500 Mbps'),
(23, 5, 'bi-cup-hot', 'Café e infusiones'),
(24, 5, 'bi-easel', 'Pizarra interactiva'),
(25, 5, 'bi-snow', 'Climatización inteligente'),
(26, 6, 'bi-tree', 'Ambiente verde'),
(27, 6, 'bi-music-note-beamed', 'Música ambiental'),
(28, 6, 'bi-cup-straw', 'Barra para catering'),
(29, 6, 'bi-shield-check', 'Seguridad 24h'),
(30, 6, 'bi-wifi', 'WiFi libre');


INSERT INTO servicio_imagenes (id, servicio_id, url) VALUES
(1, 1, 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1000&q=80'),
(2, 1, 'https://images.unsplash.com/photo-1527192491265-7e15c55b1ed2?auto=format&fit=crop&w=1000&q=80'),
(3, 1, 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1000&q=80'),
(4, 2, 'https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=1000&q=80'),
(5, 2, 'https://images.unsplash.com/photo-1517457373958-b7bdd4587205?auto=format&fit=crop&w=1000&q=80'),
(6, 2, 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1000&q=80'),
(7, 3, 'https://images.unsplash.com/photo-1511578314322-379afb476865?auto=format&fit=crop&w=1000&q=80'),
(8, 3, 'https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=1000&q=80'),
(9, 3, 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1000&q=80'),
(10, 4, 'https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=1000&q=80'),
(11, 4, 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1000&q=80'),
(12, 4, 'https://images.unsplash.com/photo-1527192491265-7e15c55b1ed2?auto=format&fit=crop&w=1000&q=80'),
(13, 5, 'https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=1000&q=80'),
(14, 5, 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1000&q=80'),
(15, 5, 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1000&q=80'),
(16, 6, 'https://images.unsplash.com/photo-1517457373958-b7bdd4587205?auto=format&fit=crop&w=1000&q=80'),
(17, 6, 'https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=1000&q=80'),
(18, 6, 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1000&q=80');


-- Usuarios ficticios locales. Passwords temporales en texto plano para el login actual; migrar a hash en fase 6. No usar en produccion.

INSERT INTO usuarios (id, nombres, apellidos, correo, telefono, password, rol_id) VALUES
(1, 'Admin', 'SpaceHub', 'admin@spacehub.pe', '999888777', 'admin123', 1),
(2, 'Gerardo', 'Guevara', 'gerardo@correo.com', '999000001', 'demo12345', 2),
(3, 'Juan De Dios', 'Chiu', 'juan@correo.com', '999000002', 'demo12345', 2),
(4, 'Jahyre', 'Turin', 'jahyre@correo.com', '999000003', 'demo12345', 2);


INSERT INTO reservas (id, servicio_id, usuario_id, fecha, hora_inicio, hora_fin, total, estado) VALUES
(101, 2, 2, '2026-09-12', '14:00:00', '18:00:00', 760.0, 'Aprobada'),
(102, 1, 3, '2026-09-15', '09:00:00', '14:00:00', 225.0, 'Pendiente'),
(103, 4, 4, '2026-09-18', '10:00:00', '16:00:00', 480.0, 'Pendiente');


COMMIT;
