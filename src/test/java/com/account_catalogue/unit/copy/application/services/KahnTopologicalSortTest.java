package com.account_catalogue.unit.copy.application.services;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.copy.application.services.KahnTopologicalSort;
import com.account_catalogue.copy.domain.exceptions.TopologicalSortCycleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests del algoritmo Kahn de ordenamiento topológico para jerarquía de cuentas.
 * TDD — RED → GREEN → REFACTOR.
 */
class KahnTopologicalSortTest {

    private KahnTopologicalSort sort;

    @BeforeEach
    void setUp() {
        sort = new KahnTopologicalSort();
    }

    // ----------------------------------------------------------------
    // Scenario: lista vacía
    // ----------------------------------------------------------------
    @Test
    @DisplayName("lista vacía retorna lista vacía")
    void listaVacia_retornaListaVacia() {
        List<AccountCatalogueEntity> resultado = sort.ordenar(Collections.emptyList());
        assertThat(resultado).isEmpty();
    }

    // ----------------------------------------------------------------
    // Scenario: todas raíz (sin jerarquía)
    // ----------------------------------------------------------------
    @Test
    @DisplayName("cinco cuentas raíz sin parent_id — todas presentes en resultado")
    void cincoCuentasRaiz_todasPresentesEnResultado() {
        List<AccountCatalogueEntity> cuentas = Arrays.asList(
                cuenta(1L, null), cuenta(2L, null), cuenta(3L, null),
                cuenta(4L, null), cuenta(5L, null)
        );
        List<AccountCatalogueEntity> resultado = sort.ordenar(cuentas);
        assertThat(resultado).hasSize(5);
        assertThat(resultado).containsExactlyInAnyOrderElementsOf(cuentas);
    }

    // ----------------------------------------------------------------
    // Scenario: padre antes que hijo (depth 2)
    // ----------------------------------------------------------------
    @Test
    @DisplayName("R → H1, H2 — R aparece antes que H1 y H2")
    void padreAntesQueHijos_depth2() {
        AccountCatalogueEntity r = cuenta(1L, null);
        AccountCatalogueEntity h1 = cuenta(2L, 1L);
        AccountCatalogueEntity h2 = cuenta(3L, 1L);

        List<AccountCatalogueEntity> resultado = sort.ordenar(Arrays.asList(h2, h1, r));

        assertThat(resultado).hasSize(3);
        assertThat(indexOf(resultado, r)).isLessThan(indexOf(resultado, h1));
        assertThat(indexOf(resultado, r)).isLessThan(indexOf(resultado, h2));
    }

    // ----------------------------------------------------------------
    // Scenario: depth 3 — R → H1 → N
    // ----------------------------------------------------------------
    @Test
    @DisplayName("depth 3: R→H1→N — orden R, H1, N")
    void profundidadTres_ordenCorrecto() {
        AccountCatalogueEntity r  = cuenta(1L, null);
        AccountCatalogueEntity h1 = cuenta(2L, 1L);
        AccountCatalogueEntity n  = cuenta(3L, 2L);

        List<AccountCatalogueEntity> resultado = sort.ordenar(Arrays.asList(n, r, h1));

        assertThat(indexOf(resultado, r)).isLessThan(indexOf(resultado, h1));
        assertThat(indexOf(resultado, h1)).isLessThan(indexOf(resultado, n));
    }

    // ----------------------------------------------------------------
    // Scenario: múltiples raíces
    // ----------------------------------------------------------------
    @Test
    @DisplayName("múltiples raíces — todas antes que sus hijos respectivos")
    void multiplesRaices_cadaRaizAntesQueSusHijos() {
        AccountCatalogueEntity r1 = cuenta(1L, null);
        AccountCatalogueEntity r2 = cuenta(2L, null);
        AccountCatalogueEntity h1 = cuenta(3L, 1L);
        AccountCatalogueEntity h2 = cuenta(4L, 2L);

        List<AccountCatalogueEntity> resultado = sort.ordenar(Arrays.asList(h1, h2, r2, r1));

        assertThat(indexOf(resultado, r1)).isLessThan(indexOf(resultado, h1));
        assertThat(indexOf(resultado, r2)).isLessThan(indexOf(resultado, h2));
    }

    // ----------------------------------------------------------------
    // Scenario: ciclo detectado A → B → A
    // ----------------------------------------------------------------
    @Test
    @DisplayName("ciclo A→B→A lanza TopologicalSortCycleException")
    void cicloAB_lanzaExcepcion() {
        // Nota: para simular ciclo en la implementación usamos parent IDs cruzados
        // La entidad JPA NO tiene parent directo en este contexto plano, usamos
        // el utilitario buildConParentId que manipula el campo parent.id vía builder
        AccountCatalogueEntity a = cuentaConParentId(1L, 2L);
        AccountCatalogueEntity b = cuentaConParentId(2L, 1L);

        assertThatThrownBy(() -> sort.ordenar(Arrays.asList(a, b)))
                .isInstanceOf(TopologicalSortCycleException.class)
                .hasMessageContaining("ciclo detectado");
    }

    // ----------------------------------------------------------------
    // Scenario: padre ausente del conjunto — hijo se inserta sin parent
    // (la implementación trata padres faltantes como raíz)
    // ----------------------------------------------------------------
    @Test
    @DisplayName("padre ausente del conjunto — cuenta tratada como raíz")
    void padreAusenteDelConjunto_cuentaTratadaComoRaiz() {
        // H referencia parent_id=9999 que NO está en el conjunto
        AccountCatalogueEntity h = cuentaConParentId(1L, 9999L);

        List<AccountCatalogueEntity> resultado = sort.ordenar(Collections.singletonList(h));

        // Debe incluir H (sin lanzar excepción) porque el padre ausente no produce ciclo
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private AccountCatalogueEntity cuenta(Long id, Long parentId) {
        AccountCatalogueEntity e = AccountCatalogueEntity.builder()
                .id(id)
                .code("COD-" + id)
                .idEnterprise("emp-origen")
                .build();
        if (parentId != null) {
            AccountCatalogueEntity p = AccountCatalogueEntity.builder().id(parentId).build();
            e.setParent(p);
        }
        return e;
    }

    private AccountCatalogueEntity cuentaConParentId(Long id, Long parentId) {
        return cuenta(id, parentId);
    }

    private int indexOf(List<AccountCatalogueEntity> lista, AccountCatalogueEntity e) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId().equals(e.getId())) return i;
        }
        return -1;
    }
}
