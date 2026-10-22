package com.spacehub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:spacehub;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
class SpacehubApplicationTests {
    @org.springframework.beans.factory.annotation.Autowired
    private com.spacehub.service.ServicioService servicioService;
    @org.springframework.beans.factory.annotation.Autowired
    private com.spacehub.service.UsuarioService usuarioService;
    @org.springframework.beans.factory.annotation.Autowired
    private com.spacehub.service.ReservaService reservaService;
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.test.web.servlet.MockMvc mvc;
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.web.context.WebApplicationContext webContext;
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.transaction.support.TransactionTemplate transacciones;
    private Long adminPruebas;
    @org.junit.jupiter.api.BeforeEach
    void prepararSesionAdmin() {
        adminPruebas = transacciones.execute(status -> {
            var rol = new com.spacehub.model.Rol(null, "ADMIN"); entityManager.persist(rol);
            var usuario = new com.spacehub.model.Usuario(null,"Admin","Prueba","admin-test@example.com","999000000","clave-demo",rol);
            entityManager.persist(usuario); entityManager.flush(); return usuario.getId();
        });
        var sesion = new org.springframework.mock.web.MockHttpSession();
        sesion.setAttribute("usuarioId", adminPruebas); sesion.setAttribute("csrf", "token-prueba");
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(webContext)
                .defaultRequest(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/")
                        .session(sesion).param("_csrf","token-prueba")).build();
    }
    @org.junit.jupiter.api.AfterEach
    void limpiarAdmin() {
        transacciones.executeWithoutResult(status -> {
            var admin = entityManager.find(com.spacehub.model.Usuario.class, adminPruebas);
            if (admin != null) {
                entityManager.createQuery("delete from Reserva r where r.usuario.id=:id").setParameter("id",adminPruebas).executeUpdate();
                var rol = admin.getRol(); entityManager.remove(admin); entityManager.flush(); entityManager.remove(rol); }
        });
    }

    @Test
    void contactoValidaCamposYNoConfirmaEnvioInexistente() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/contacto"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/contacto")
                .param("nombre"," ").param("correo","invalido").param("motivo","inventado").param("mensaje","corto"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attributeHasFieldErrors("contactoForm","nombre","correo","motivo","mensaje"));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/contacto")
                .param("nombre","Cliente Prueba").param("correo","cliente@example.com").param("motivo","reserva").param("mensaje","Consulta de disponibilidad de un espacio."))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/contacto"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attribute("mensaje","Datos validados. Para enviar tu consulta, utiliza nuestros canales de contacto."));
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void reservaRechazaImporteFueraDelDecimalDeMySQL() {
        var c=new com.spacehub.model.Categoria(null,"Importes","bi-house","");entityManager.persist(c);
        var e=new com.spacehub.model.Servicio(null,"Tarifa maxima",c,"Lima","Direccion","",99999999.99,5,"Disponible","","",new java.util.ArrayList<>(),null);entityManager.persist(e);entityManager.flush();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->reservaService.crearReserva(e.getId(),adminPruebas,
                java.time.LocalDate.now(java.time.ZoneId.of("America/Lima")).plusDays(2),java.time.LocalTime.of(10,0),java.time.LocalTime.of(12,0)));
        org.junit.jupiter.api.Assertions.assertEquals(0,reservaService.obtenerTotalReservas());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void crudReservasCrucesEstadosImportesYFechas() throws Exception {
        var categoria = new com.spacehub.model.Categoria(null,"Reservas CRUD","bi-house",""); entityManager.persist(categoria);
        var espacio = new com.spacehub.model.Servicio(null,"Reserva CRUD",categoria,"Lima","Direccion","",45.50,5,"Disponible","","",new java.util.ArrayList<>(),null); entityManager.persist(espacio); entityManager.flush();
        var fecha=java.time.LocalDate.now(java.time.ZoneId.of("America/Lima")).plusDays(5);
        var r=reservaService.crearReserva(espacio.getId(),adminPruebas,fecha,java.time.LocalTime.of(10,0),java.time.LocalTime.of(11,30));
        org.junit.jupiter.api.Assertions.assertEquals(new java.math.BigDecimal("68.25"),r.getTotalDecimal());
        org.junit.jupiter.api.Assertions.assertEquals("1 h 30 min",r.getDuracionTexto());
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->reservaService.crearReserva(espacio.getId(),adminPruebas,fecha,java.time.LocalTime.of(11,0),java.time.LocalTime.of(12,0)));
        var consecutiva=reservaService.crearReserva(espacio.getId(),adminPruebas,fecha,java.time.LocalTime.of(11,30),java.time.LocalTime.of(12,30));
        for(String ruta:java.util.List.of("/admin/reservas","/admin/reservas/editar/"+r.getId()))
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(ruta)).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/reservas/estado/"+r.getId()).param("estado","Aprobada"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attributeExists("mensaje"));
        org.junit.jupiter.api.Assertions.assertEquals("Aprobada",r.getEstado());
        espacio.setPrecioPorHora(50);entityManager.flush();
        reservaService.cambiarEstado(r.getId(),"Rechazada");
        org.junit.jupiter.api.Assertions.assertEquals(new java.math.BigDecimal("68.25"),r.getTotalDecimal());
        var reemplazo=reservaService.crearReserva(espacio.getId(),adminPruebas,fecha,java.time.LocalTime.of(10,0),java.time.LocalTime.of(11,30));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->reservaService.cambiarEstado(r.getId(),"Aprobada"));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/reservas/guardar")
                .param("id",r.getId().toString()).param("fecha",fecha.toString()).param("horaInicio","13:00").param("horaFin","14:30").param("estado","Pendiente"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/admin/reservas"));
        org.junit.jupiter.api.Assertions.assertEquals(new java.math.BigDecimal("75.00"),r.getTotalDecimal());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/reservas/guardar")
                .param("id",r.getId().toString()).param("fecha",fecha.toString()).param("horaInicio","15:00").param("horaFin","14:00").param("estado","Inventado"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attributeHasFieldErrors("reservaForm","horarioValido","estado"));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->reservaService.crearReserva(espacio.getId(),adminPruebas,fecha.minusDays(10),java.time.LocalTime.of(9,0),java.time.LocalTime.of(10,0)));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/reservas/eliminar/"+r.getId()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isMethodNotAllowed());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/reservas/eliminar/"+r.getId()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attributeExists("mensaje"));
        entityManager.clear(); org.junit.jupiter.api.Assertions.assertNull(entityManager.find(com.spacehub.model.Reserva.class,r.getId()));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/reservas/editar/"+r.getId()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void solicitudesSimultaneasNoDuplicanHorario() throws Exception {
        Long[] ids=transacciones.execute(status->{
            var c=new com.spacehub.model.Categoria(null,"Concurrencia","bi-house","");entityManager.persist(c);
            var e=new com.spacehub.model.Servicio(null,"Simultaneas",c,"Lima","Direccion","",20,5,"Disponible","","",new java.util.ArrayList<>(),null);entityManager.persist(e);entityManager.flush();
            return new Long[]{c.getId(),e.getId()};
        });
        var ejecutor=java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            var salida=new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.Callable<Boolean> intento=()->{
                salida.await();
                try { reservaService.crearReserva(ids[1],adminPruebas,java.time.LocalDate.now(java.time.ZoneId.of("America/Lima")).plusDays(10),java.time.LocalTime.of(10,0),java.time.LocalTime.of(11,0)); return true; }
                catch(IllegalArgumentException ex){ return false; }
            };
            var uno=ejecutor.submit(intento);var dos=ejecutor.submit(intento);salida.countDown();
            org.junit.jupiter.api.Assertions.assertNotEquals(uno.get(15,java.util.concurrent.TimeUnit.SECONDS),dos.get(15,java.util.concurrent.TimeUnit.SECONDS));
        } finally {
            ejecutor.shutdownNow();ejecutor.awaitTermination(15,java.util.concurrent.TimeUnit.SECONDS);
            transacciones.executeWithoutResult(status->{
                entityManager.createQuery("delete from Reserva r where r.servicio.id=:id").setParameter("id",ids[1]).executeUpdate();
                entityManager.remove(entityManager.find(com.spacehub.model.Servicio.class,ids[1]));entityManager.flush();
                entityManager.remove(entityManager.find(com.spacehub.model.Categoria.class,ids[0]));
            });
        }
    }

    @Test
    void sesionAutenticadaCargaRolSinTransaccionDePrueba() throws Exception {
        org.junit.jupiter.api.Assertions.assertFalse(org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/categorias"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void sesionesPermisosCsrfMigracionEHistorial() throws Exception {
        var libre = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(webContext).build();
        libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/administracion"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/login"));
        libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/reservas"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/login"));
        var rol = new com.spacehub.model.Rol(null,"CLIENTE"); entityManager.persist(rol);
        var cliente = new com.spacehub.model.Usuario(null,"Ana","Prueba","ana@example.com","999000000","antigua123",rol);
        entityManager.persist(cliente); entityManager.flush();
        var sesion = (org.springframework.mock.web.MockHttpSession) libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/login"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andReturn().getRequest().getSession();
        String token = (String) sesion.getAttribute("csrf"); String anteriorId = sesion.getId();
        libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/login").session(sesion)
                .param("correo","ana@example.com").param("password","antigua123"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isForbidden());
        libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/login").session(sesion).param("_csrf",token)
                .param("correo","ana@example.com").param("password","incorrecta"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attributeExists("error"));
        org.junit.jupiter.api.Assertions.assertEquals("antigua123",cliente.getPassword());
        var acceso = libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/login").session(sesion).param("_csrf",token)
                .param("correo","ana@example.com").param("password","antigua123"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/"))
                .andReturn();
        var conectada = (org.springframework.mock.web.MockHttpSession) acceso.getRequest().getSession();
        org.junit.jupiter.api.Assertions.assertNotEquals(anteriorId,conectada.getId());
        org.junit.jupiter.api.Assertions.assertEquals(cliente.getId(),conectada.getAttribute("usuarioId"));
        org.junit.jupiter.api.Assertions.assertTrue(cliente.getPassword().startsWith("$2"));
        org.junit.jupiter.api.Assertions.assertTrue(usuarioService.validarLogin("ana@example.com","antigua123").isPresent());
        for (String ruta : java.util.List.of("/administracion","/admin/categorias","/admin/servicios/nuevo"))
            libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(ruta).session(conectada))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isForbidden());
        var categoria = new com.spacehub.model.Categoria(null,"Sesion","bi-house",""); entityManager.persist(categoria);
        var espacio = new com.spacehub.model.Servicio(null,"Espacio sesion",categoria,"Lima","Direccion","",20,5,"Disponible","","",new java.util.ArrayList<>(),null);
        entityManager.persist(espacio); entityManager.flush();
        var otro = new com.spacehub.model.Usuario(null,"Otro","Cliente","otro@example.com","999000001","demo12345",rol); entityManager.persist(otro); entityManager.flush();
        reservaService.crearReserva(espacio.getId(),otro.getId(),java.time.LocalDate.of(2026,12,1),java.time.LocalTime.of(9,0),java.time.LocalTime.of(10,0));
        libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/reservas").session(conectada)
                .param("_csrf",(String)conectada.getAttribute("csrf")).param("servicioId",espacio.getId().toString())
                .param("fecha","2026-12-02").param("horaInicio","10:00").param("horaFin","11:00")
                .param("clienteCorreo","otro@example.com").param("usuarioId",otro.getId().toString()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/reservas"));
        org.junit.jupiter.api.Assertions.assertEquals(1,reservaService.listarPorUsuario(cliente.getId()).size());
        libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/reservas").session(conectada))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Otro Cliente"))));
        libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/logout").session(conectada))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isForbidden());
        libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/logout").session(conectada).param("_csrf",(String)conectada.getAttribute("csrf")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/login"));
        org.junit.jupiter.api.Assertions.assertTrue(conectada.isInvalid());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void registroValidaYGuardaHashSinPermitirRolAdmin() throws Exception {
        var libre = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(webContext).build();
        entityManager.persist(new com.spacehub.model.Rol(null,"CLIENTE")); entityManager.flush();
        var sesion = (org.springframework.mock.web.MockHttpSession) libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/registro"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk()).andReturn().getRequest().getSession();
        String token = (String) sesion.getAttribute("csrf");
        libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/registro").session(sesion).param("_csrf",token)
                .param("nombres","x").param("apellidos","").param("correo","invalido").param("telefono","123").param("password","corta").param("tipo","admin"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attributeHasFieldErrors("registroForm","nombres","apellidos","correo","telefono","password","tipo","terminos"));
        libre.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/registro").session(sesion).param("_csrf",token)
                .param("nombres","Nueva").param("apellidos","Cliente").param("correo","NUEVA@example.com").param("telefono","999000000")
                .param("password","nueva12345").param("tipo","cliente").param("terminos","true").param("rol","ADMIN"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/login"));
        var u = usuarioService.validarLogin("nueva@example.com","nueva12345").orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("CLIENTE",u.getRol().getNombre());
        org.junit.jupiter.api.Assertions.assertTrue(u.getPassword().startsWith("$2"));
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void crudEspaciosConFotosComodidadesYReservas() throws Exception {
        var categoria = new com.spacehub.model.Categoria(null, "Espacios CRUD", "bi-house", "Prueba");
        entityManager.persist(categoria); entityManager.flush();
        var alta = org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/servicios/guardar")
                .param("nombre", "Nuevo espacio CRUD").param("categoriaId", categoria.getId().toString())
                .param("distrito", "Lima").param("direccion", "Av. Prueba 123")
                .param("precioPorHora", "35.50").param("capacidad", "12").param("estado", "Disponible")
                .param("imagenes", "https://example.com/foto.jpg")
                .param("comodidades", "bi-wifi|WiFi\nbi-cup-hot|Cafe");
        mvc.perform(alta).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/admin/servicios"));
        entityManager.clear();
        var espacio = entityManager.createQuery("select s from Servicio s where s.nombre = 'Nuevo espacio CRUD'", com.spacehub.model.Servicio.class).getSingleResult();
        Long id = espacio.getId();
        org.junit.jupiter.api.Assertions.assertEquals(2, espacio.getServicios().size());
        org.junit.jupiter.api.Assertions.assertEquals("https://example.com/foto.jpg", espacio.getImagenes().get(0));
        for (String ruta : java.util.List.of("/admin/servicios", "/admin/servicios/nuevo", "/admin/servicios/editar/" + id, "/servicios/" + id)) {
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(ruta))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        }
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/servicios/guardar")
                .param("nombre", "x").param("precioPorHora", "0").param("capacidad", "1001")
                .param("imagenes", "javascript:alert(1)").param("comodidades", "sin separador"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attributeHasFieldErrors(
                        "servicioForm", "nombre", "categoriaId", "precioPorHora", "capacidad", "imagenesValidas", "comodidadesValidas"));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/servicios/guardar")
                .param("id", id.toString()).param("nombre", "Espacio editado").param("categoriaId", categoria.getId().toString())
                .param("distrito", "Surco").param("direccion", "Av. Prueba 456").param("precioPorHora", "40.25")
                .param("capacidad", "15").param("estado", "Ocupado").param("imagenes", "https://example.com/nueva.jpg")
                .param("comodidades", "bi-snow|Aire acondicionado"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/admin/servicios"));
        entityManager.clear(); espacio = entityManager.find(com.spacehub.model.Servicio.class, id);
        org.junit.jupiter.api.Assertions.assertEquals("Espacio editado", espacio.getNombre());
        org.junit.jupiter.api.Assertions.assertEquals(1, espacio.getServicios().size());
        org.junit.jupiter.api.Assertions.assertEquals("https://example.com/nueva.jpg", espacio.getImagenes().get(0));
        var sinFotos = servicioService.formulario(id);
        sinFotos.setImagenes(""); sinFotos.setComodidades(""); servicioService.guardar(sinFotos);
        entityManager.clear(); espacio = entityManager.find(com.spacehub.model.Servicio.class,id);
        org.junit.jupiter.api.Assertions.assertTrue(espacio.getImagenes().isEmpty());
        org.junit.jupiter.api.Assertions.assertTrue(espacio.getServicios().isEmpty());
        for (String ruta : java.util.List.of("/", "/servicios", "/servicios/"+id)) {
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(ruta))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Sin fotos disponibles")));
        }
        var rol = new com.spacehub.model.Rol(null, "CLIENTE_CRUD"); entityManager.persist(rol);
        var usuario = new com.spacehub.model.Usuario(null,"Cliente","Prueba","crud@example.com","999000000","demo12345",rol); entityManager.persist(usuario);
        var reserva = new com.spacehub.model.Reserva(null,espacio,"","",java.time.LocalDate.of(2026,12,1),java.time.LocalTime.of(10,0),java.time.LocalTime.of(11,0),40.25,"Pendiente");
        reserva.setUsuario(usuario); entityManager.persist(reserva); entityManager.flush();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/servicios/eliminar/"+id))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attributeExists("error"));
        org.junit.jupiter.api.Assertions.assertNotNull(entityManager.find(com.spacehub.model.Servicio.class,id));
        entityManager.remove(reserva); entityManager.flush();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/servicios/eliminar/"+id))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isMethodNotAllowed());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/servicios/eliminar/"+id))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attributeExists("mensaje"));
        entityManager.clear();
        org.junit.jupiter.api.Assertions.assertNull(entityManager.find(com.spacehub.model.Servicio.class,id));
        org.junit.jupiter.api.Assertions.assertEquals(0L, entityManager.createQuery("select count(i) from ServicioImagen i where i.servicio.id = :id",Long.class).setParameter("id",id).getSingleResult());
        org.junit.jupiter.api.Assertions.assertEquals(0L, entityManager.createQuery("select count(i) from ServicioIncluido i where i.servicio.id = :id",Long.class).setParameter("id",id).getSingleResult());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/servicios/editar/"+id))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void crudCategoriasConValidacionesYDependencias() throws Exception {
        var post = org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/categorias/guardar")
                .param("nombre", "  Categoria CRUD  ").param("icono", "bi-building").param("descripcion", "Descripcion de prueba");
        mvc.perform(post).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/admin/categorias"));
        entityManager.flush();
        var categoria = entityManager.createQuery("select c from Categoria c where c.nombre = :nombre", com.spacehub.model.Categoria.class)
                .setParameter("nombre", "Categoria CRUD").getSingleResult();
        Long id = categoria.getId();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/categorias"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Categoria CRUD")));
        for (String ruta : java.util.List.of("/admin/categorias/nueva", "/admin/categorias/editar/" + id)) {
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(ruta))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        }
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/categorias/guardar")
                .param("nombre", "categoria crud"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attributeHasFieldErrors("categoria", "nombre"));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/categorias/guardar")
                .param("nombre", "  ").param("icono", "clase invalida").param("descripcion", "x".repeat(201)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attributeHasFieldErrors("categoria", "nombre", "icono", "descripcion"));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/categorias/guardar")
                .param("id", id.toString()).param("nombre", "Categoria CRUD").param("icono", "bi-laptop").param("descripcion", "Editada"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/admin/categorias"));
        entityManager.clear();
        categoria = entityManager.find(com.spacehub.model.Categoria.class, id);
        org.junit.jupiter.api.Assertions.assertEquals("Editada", categoria.getDescripcion());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/categorias/eliminar/" + id))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isMethodNotAllowed());
        var servicio = new com.spacehub.model.Servicio(null, "Espacio dependiente", categoria, "Lima", "Direccion", "", 20, 5,
                "Disponible", "", "", new java.util.ArrayList<>(), null);
        entityManager.persist(servicio);
        entityManager.flush();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/categorias/eliminar/" + id))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attribute("error", "No se puede eliminar la categoría porque tiene espacios asociados."));
        org.junit.jupiter.api.Assertions.assertNotNull(entityManager.find(com.spacehub.model.Categoria.class, id));
        entityManager.remove(servicio);
        entityManager.flush();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/categorias/eliminar/" + id))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attribute("mensaje", "Categoría eliminada correctamente."));
        entityManager.clear();
        org.junit.jupiter.api.Assertions.assertNull(entityManager.find(com.spacehub.model.Categoria.class, id));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/categorias/editar/" + id))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void serviciosConsultanYGuardanEnBaseDeDatos() throws Exception {
        entityManager.persist(new com.spacehub.model.Rol(null, "CLIENTE"));
        var categoria = new com.spacehub.model.Categoria(null, "Categoria BD", "bi-house", "Prueba");
        entityManager.persist(categoria);
        var servicio = new com.spacehub.model.Servicio(null, "Espacio desde BD", categoria,
                "Lima", "Direccion", "", 45.50, 10, "Disponible", "Prueba", "Detalle",
                new java.util.ArrayList<>(), null);
        var imagen = new com.spacehub.model.ServicioImagen();
        imagen.setUrl("https://example.com/prueba.jpg");
        servicio.setImagenesPersistidas(java.util.List.of(imagen));
        entityManager.persist(servicio);
        entityManager.flush();
        entityManager.clear();
        org.junit.jupiter.api.Assertions.assertTrue(usuarioService.registrarUsuario(
                "Nuevo", "Cliente", " Nuevo@Example.com ", "999000000", "cliente", "demo12345"));
        org.junit.jupiter.api.Assertions.assertFalse(usuarioService.registrarUsuario(
                "Otro", "Cliente", "NUEVO@example.com", "999000000", "cliente", "demo12345"));
        org.junit.jupiter.api.Assertions.assertTrue(usuarioService.validarLogin("NUEVO@example.com", "demo12345").isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(usuarioService.validarLogin("nuevo@example.com", "incorrecta").isEmpty());
        org.junit.jupiter.api.Assertions.assertEquals(1, servicioService.filtrarPorCategoria("categoria bd").size());
        org.junit.jupiter.api.Assertions.assertEquals("Espacio desde BD", servicioService.obtenerDestacados().get(0).getNombre());
        var fecha = java.time.LocalDate.of(2026, 12, 1);
        var inicio = java.time.LocalTime.of(10, 0);
        var fin = java.time.LocalTime.of(11, 30);
        var reserva = reservaService.crearReserva(servicio.getId(), usuarioService.validarLogin("nuevo@example.com", "demo12345").orElseThrow().getId(), fecha, inicio, fin);
        entityManager.flush();
        entityManager.clear();
        var guardada = entityManager.find(com.spacehub.model.Reserva.class, reserva.getId());
        org.junit.jupiter.api.Assertions.assertEquals(new java.math.BigDecimal("68.25"), guardada.getTotalDecimal());
        org.junit.jupiter.api.Assertions.assertEquals("Nuevo Cliente", guardada.getClienteNombre());
        org.junit.jupiter.api.Assertions.assertEquals(1, reservaService.obtenerTotalReservas());
        org.junit.jupiter.api.Assertions.assertEquals(1, reservaService.obtenerReservasPendientesCount());
        org.junit.jupiter.api.Assertions.assertEquals(68.25, reservaService.calcularIngresosTotales());
        for (String ruta : java.util.List.of("/", "/servicios", "/servicios/" + servicio.getId(), "/reservas", "/administracion")) {
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(ruta))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        }
    }

    @Test
    void inicioNoInsertaDatosDeDemostracion() {
        org.junit.jupiter.api.Assertions.assertEquals(0, servicioService.listarTodos().size());
        org.junit.jupiter.api.Assertions.assertEquals(0, reservaService.obtenerTotalReservas());
    }

    @org.springframework.beans.factory.annotation.Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Test
    @org.springframework.transaction.annotation.Transactional
    void persisteRelacionesImagenesEImportes() {
        var rol = new com.spacehub.model.Rol(null, "CLIENTE_TEST");
        entityManager.persist(rol);
        var usuario = new com.spacehub.model.Usuario(null, "Cliente", "Prueba",
                "prueba@example.com", "999000000", "password-demo", rol);
        entityManager.persist(usuario);
        var categoria = new com.spacehub.model.Categoria(null, "Prueba", "bi-house", "Categoria de prueba");
        entityManager.persist(categoria);
        var servicio = new com.spacehub.model.Servicio(null, "Espacio de prueba", categoria,
                "Lima", "Direccion de prueba", "", 45.50, 10, "Disponible", "Prueba", "Detalle",
                new java.util.ArrayList<>(java.util.List.of(new com.spacehub.model.ServicioIncluido("bi-wifi", "WiFi"))),
                null);
        var imagen = new com.spacehub.model.ServicioImagen();
        imagen.setUrl("https://example.com/espacio.jpg");
        servicio.setImagenesPersistidas(java.util.List.of(imagen));
        entityManager.persist(servicio);
        var reserva = new com.spacehub.model.Reserva(null, servicio, "", "",
                java.time.LocalDate.of(2026, 12, 1), java.time.LocalTime.of(10, 0),
                java.time.LocalTime.of(12, 0), 91.00, "Pendiente");
        reserva.setUsuario(usuario);
        entityManager.persist(reserva);
        entityManager.flush();
        entityManager.clear();
        var guardada = entityManager.find(com.spacehub.model.Reserva.class, reserva.getId());
        org.junit.jupiter.api.Assertions.assertEquals("prueba@example.com", guardada.getClienteCorreo());
        org.junit.jupiter.api.Assertions.assertEquals("Cliente Prueba", guardada.getClienteNombre());
        org.junit.jupiter.api.Assertions.assertEquals("Prueba", guardada.getServicio().getCategoria().getNombre());
        org.junit.jupiter.api.Assertions.assertEquals(91.00, guardada.getTotal());
        org.junit.jupiter.api.Assertions.assertEquals(45.50, guardada.getServicio().getPrecioPorHora());
        org.junit.jupiter.api.Assertions.assertEquals("WiFi", guardada.getServicio().getServicios().get(0).getTexto());
        org.junit.jupiter.api.Assertions.assertEquals("https://example.com/espacio.jpg", guardada.getServicio().getImagenes().get(0));
    }


	@Test
	void contextLoads() {
	}

}
