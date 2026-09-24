package com.spacehub.service;

import com.spacehub.model.Categoria;
import com.spacehub.model.Servicio;
import com.spacehub.model.ServicioIncluido;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioService {

    private final List<Categoria> categorias = new ArrayList<>();
    private final List<Servicio> servicios = new ArrayList<>();

    public ServicioService() {
        cargarDatos();
    }

    private void cargarDatos() {
        // Categorías
        Categoria catCoworking = new Categoria(1L, "Coworking", "bi-laptop", "Mesas compartidas y oficinas privadas para trabajar cómodo.");
        Categoria catSalones = new Categoria(2L, "Salones", "bi-building", "Auditorios amplios para conferencias, talleres y capacitaciones.");
        Categoria catRooftops = new Categoria(3L, "Rooftops", "bi-sun", "Terrazas al aire libre con vista panorámica para eventos sociales.");
        Categoria catEstudios = new Categoria(4L, "Estudios", "bi-camera", "Sets profesionales con iluminación para sesiones de fotos y video.");

        categorias.add(catCoworking);
        categorias.add(catSalones);
        categorias.add(catRooftops);
        categorias.add(catEstudios);

        // Espacio 1: Coworking Loft San Isidro
        servicios.add(new Servicio(
                1L,
                "Coworking Loft San Isidro",
                catCoworking,
                "San Isidro",
                "Av. Las Camelias 450, San Isidro - Lima",
                "San Isidro, a 1 cuadra de la estación Canaval y Moreyra del Metropolitano.",
                45.00,
                15,
                "Disponible",
                "Luz natural, escritorios ergonómicos, café ilimitado y zona chill out para trabajar cómodo.",
                "Ambiente cálido con luz natural, mesas amplias de madera, café de especialidad ilimitado y zona chill out para trabajar cómodo y productivo. Espacio ideal para freelancers, programadores y equipos que buscan concentrarse sin interrupciones.",
                Arrays.asList(
                        new ServicioIncluido("bi-wifi", "WiFi 500 Mbps"),
                        new ServicioIncluido("bi-cup-hot", "Café ilimitado"),
                        new ServicioIncluido("bi-easel", "Pizarra acrílica"),
                        new ServicioIncluido("bi-snow", "Aire acondicionado"),
                        new ServicioIncluido("bi-shield-check", "Seguridad 24h")
                ),
                Arrays.asList(
                        "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1527192491265-7e15c55b1ed2?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1000&q=80"
                )
        ));

        // Espacio 2: Rooftop Sunset Miraflores
        servicios.add(new Servicio(
                2L,
                "Rooftop Sunset Miraflores",
                catRooftops,
                "Miraflores",
                "Av. Larco 1230, Miraflores - Lima",
                "Miraflores, a 2 cuadras del Parque del Amor y la Costa Verde.",
                190.00,
                70,
                "Disponible",
                "Terraza exclusiva con vista al mar. Equipado con sillones lounge, barra y sistema de sonido.",
                "Disfruta de atardeceres espectaculares frente al océano en nuestra terraza privada en Miraflores. Cuenta con iluminación ambiental cálida, cómodos sillones lounge, barra para coctelería y equipo de sonido envolvente.",
                Arrays.asList(
                        new ServicioIncluido("bi-sun", "Vista al mar"),
                        new ServicioIncluido("bi-music-note-beamed", "Sistema de sonido"),
                        new ServicioIncluido("bi-cup-straw", "Barra equipada"),
                        new ServicioIncluido("bi-shield-check", "Seguridad 24h"),
                        new ServicioIncluido("bi-wifi", "WiFi alta velocidad")
                ),
                Arrays.asList(
                        "https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1000&q=80"
                )
        ));

        // Espacio 3: Auditorio Magna Surco
        servicios.add(new Servicio(
                3L,
                "Auditorio Magna Surco",
                catSalones,
                "Surco",
                "Av. Primavera 890, Surco - Lima",
                "Surco, cerca al Centro Comercial Chacarilla y Av. Encalada.",
                220.00,
                120,
                "Disponible",
                "Auditorio acústico con butacas cómodas, podio para expositores y proyector láser 4K.",
                "Auditorio acústico moderno diseñado para ponencias de alto impacto, lanzamientos de marca y capacitaciones corporativas. Equipado con pantallas gigantes, micrófonos inalámbricos y climatización centralizada.",
                Arrays.asList(
                        new ServicioIncluido("bi-display", "Proyector láser 4K"),
                        new ServicioIncluido("bi-mic", "Podio y micrófonos"),
                        new ServicioIncluido("bi-wifi", "WiFi alta velocidad"),
                        new ServicioIncluido("bi-snow", "Aire acondicionado"),
                        new ServicioIncluido("bi-shield-check", "Seguridad 24h")
                ),
                Arrays.asList(
                        "https://images.unsplash.com/photo-1511578314322-379afb476865?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1000&q=80"
                )
        ));

        // Espacio 4: Studio Click Barranco
        servicios.add(new Servicio(
                4L,
                "Studio Click Barranco",
                catEstudios,
                "Barranco",
                "Av. Pedro de Osma 210, Barranco - Lima",
                "Barranco, a pocos pasos de la Plaza Principal y el Puente de los Suspiros.",
                80.00,
                20,
                "Disponible",
                "Estudio profesional para sesiones de fotografía y video. Incluye ciclorama blanco e iluminación.",
                "Estudio fotográfico completamente equipado con ciclorama sin fin de 5 metros de ancho, luces continuas y flashes de alta potencia. Espacio versátil ideal para producciones de moda, anuncios comerciales y contenido digital.",
                Arrays.asList(
                        new ServicioIncluido("bi-camera", "Ciclorama blanco"),
                        new ServicioIncluido("bi-lightbulb", "Iluminación continua"),
                        new ServicioIncluido("bi-wifi", "WiFi 500 Mbps"),
                        new ServicioIncluido("bi-snow", "Aire acondicionado"),
                        new ServicioIncluido("bi-easel", "Camerino y maquillaje")
                ),
                Arrays.asList(
                        "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1527192491265-7e15c55b1ed2?auto=format&fit=crop&w=1000&q=80"
                )
        ));

        // Espacio 5: Sala Boardroom Empresarial
        servicios.add(new Servicio(
                5L,
                "Sala Boardroom Empresarial",
                catCoworking,
                "San Isidro",
                "Calle Dean Valdivia 180, San Isidro - Lima",
                "San Isidro, pleno centro financiero a espaldas de Av. Rivera Navarrete.",
                60.00,
                12,
                "Disponible",
                "Sala de directorio privada para 12 personas con pantalla 65'' para videollamadas Zoom.",
                "Sala ejecutivo-corporativa elegante aislada acústicamente, idónea para reuniones de junta, presentaciones a clientes y videoconferencias internacionales. Incluye estación de café gourmet y asistencia ejecutiva.",
                Arrays.asList(
                        new ServicioIncluido("bi-display", "Pantalla 65'' Zoom"),
                        new ServicioIncluido("bi-wifi", "WiFi 500 Mbps"),
                        new ServicioIncluido("bi-cup-hot", "Café e infusiones"),
                        new ServicioIncluido("bi-easel", "Pizarra interactiva"),
                        new ServicioIncluido("bi-snow", "Climatización inteligente")
                ),
                Arrays.asList(
                        "https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1000&q=80"
                )
        ));

        // Espacio 6: Terraza Bohemia Barranco
        servicios.add(new Servicio(
                6L,
                "Terraza Bohemia Barranco",
                catRooftops,
                "Barranco",
                "Av. Grau 405, Barranco - Lima",
                "Barranco, con vista privilegiada al boulevard cultural y turístico.",
                160.00,
                50,
                "Disponible",
                "Ambiente bohemio al aire libre con plantas, sillones cómodos y vista al boulevard de Barranco.",
                "Un oasis urbano en el corazón de Barranco. Terraza rodeada de vegetación nativa, ambientación vintage y luces de guirnalda que crean una atmósfera acogedora e inolvidable para eventos privados y corporativos.",
                Arrays.asList(
                        new ServicioIncluido("bi-tree", "Ambiente verde"),
                        new ServicioIncluido("bi-music-note-beamed", "Música ambiental"),
                        new ServicioIncluido("bi-cup-straw", "Barra para catering"),
                        new ServicioIncluido("bi-shield-check", "Seguridad 24h"),
                        new ServicioIncluido("bi-wifi", "WiFi libre")
                ),
                Arrays.asList(
                        "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=1000&q=80",
                        "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1000&q=80"
                )
        ));
    }

    public List<Servicio> listarTodos() {
        return servicios;
    }

    public Servicio buscarPorId(Long id) {
        return servicios.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Servicio> filtrarPorCategoria(String categoriaNombre) {
        if (categoriaNombre == null || categoriaNombre.isBlank() || categoriaNombre.equalsIgnoreCase("Todos")) {
            return listarTodos();
        }
        return servicios.stream()
                .filter(s -> s.getCategoria().getNombre().equalsIgnoreCase(categoriaNombre))
                .collect(Collectors.toList());
    }

    public List<Servicio> obtenerDestacados() {
        return servicios.stream().limit(3).collect(Collectors.toList());
    }

    public List<Categoria> listarCategorias() {
        return categorias;
    }
}
