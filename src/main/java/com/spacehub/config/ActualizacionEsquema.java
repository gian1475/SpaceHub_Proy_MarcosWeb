package com.spacehub.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

/** Actualización aditiva antes de que Hibernate valide una instalación existente. */
@Configuration
public class ActualizacionEsquema {
    @Bean static BeanFactoryPostProcessor ordenarActualizacion() {
        return factory -> {
            var definition = factory.getBeanDefinition("entityManagerFactory");
            var dependencias = new java.util.ArrayList<String>();
            if (definition.getDependsOn() != null) dependencias.addAll(java.util.List.of(definition.getDependsOn()));
            dependencias.add("actualizarEsquema"); definition.setDependsOn(dependencias.toArray(String[]::new));
        };
    }
    @Bean InitializingBean actualizarEsquema(DataSource dataSource) {
        return () -> {
            try (var connection = dataSource.getConnection()) {
                if (!connection.getMetaData().getDatabaseProductName().equalsIgnoreCase("MySQL")) return;
                try (var statement = connection.createStatement()) {
                    try (var columns = connection.getMetaData().getColumns(connection.getCatalog(), null, "usuarios", "activo")) {
                        if (!columns.next()) statement.executeUpdate("ALTER TABLE usuarios ADD COLUMN activo BIT NOT NULL DEFAULT 1");
                    }
                    try (var columns = connection.getMetaData().getColumns(connection.getCatalog(), null, "usuarios", "version_token")) {
                        if (!columns.next()) statement.executeUpdate("ALTER TABLE usuarios ADD COLUMN version_token BIGINT NOT NULL DEFAULT 0");
                    }
                    statement.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS mensajes_contacto (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(100) NOT NULL,
                        correo VARCHAR(100) NOT NULL, motivo VARCHAR(20) NOT NULL,
                        mensaje VARCHAR(2000) NOT NULL, fecha DATETIME(6) NOT NULL,
                        atendido BIT NOT NULL DEFAULT 0) ENGINE=InnoDB
                        """);
                    try (var columns = connection.getMetaData().getColumns(connection.getCatalog(), null, "servicios", "propietario_id")) {
                        if (!columns.next()) statement.executeUpdate("""
                            ALTER TABLE servicios ADD COLUMN propietario_id BIGINT NULL,
                            ADD CONSTRAINT fk_servicios_propietario FOREIGN KEY (propietario_id)
                            REFERENCES usuarios(id) ON DELETE RESTRICT ON UPDATE RESTRICT
                            """);
                    }
                }
            }
        };
    }
}
