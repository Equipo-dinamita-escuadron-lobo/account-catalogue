package com.account_catalogue.unit.copy.application.services;

import com.account_catalogue.copy.application.services.EquivalenceMapper;
import com.account_catalogue.copy.domain.models.CopyEquivalencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests del mapper de equivalencias idViejo → idNuevo.
 */
class EquivalenceMapperTest {

    private EquivalenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EquivalenceMapper();
    }

    @Test
    @DisplayName("agregar equivalencia y recuperarla correctamente")
    void agregarYRecuperar() {
        mapper.registrar("account", 1L, 100L);

        Long nuevoId = mapper.resolverNuevoId("account", 1L);

        assertThat(nuevoId).isEqualTo(100L);
    }

    @Test
    @DisplayName("ID no registrado retorna null")
    void idNoRegistrado_retornaNull() {
        Long resultado = mapper.resolverNuevoId("account", 9999L);
        assertThat(resultado).isNull();
    }

    @Test
    @DisplayName("múltiples tablas — no hay colisión de keys")
    void multipleTablas_sinColision() {
        mapper.registrar("account", 1L, 100L);
        mapper.registrar("tax", 1L, 200L);

        assertThat(mapper.resolverNuevoId("account", 1L)).isEqualTo(100L);
        assertThat(mapper.resolverNuevoId("tax", 1L)).isEqualTo(200L);
    }

    @Test
    @DisplayName("toList retorna todas las equivalencias registradas")
    void toList_retornaTodasLasEquivalencias() {
        mapper.registrar("account", 1L, 100L);
        mapper.registrar("account", 2L, 101L);
        mapper.registrar("tax", 3L, 200L);

        List<CopyEquivalencia> lista = mapper.toList();

        assertThat(lista).hasSize(3);
    }

    @Test
    @DisplayName("limpiar reinicia el estado")
    void limpiar_reiniciaEstado() {
        mapper.registrar("account", 1L, 100L);
        mapper.limpiar();

        assertThat(mapper.toList()).isEmpty();
        assertThat(mapper.resolverNuevoId("account", 1L)).isNull();
    }
}
