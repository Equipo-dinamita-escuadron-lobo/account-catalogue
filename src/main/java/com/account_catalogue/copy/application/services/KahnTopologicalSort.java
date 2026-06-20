package com.account_catalogue.copy.application.services;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.copy.application.output.ITopologicalSortPort;
import com.account_catalogue.copy.domain.exceptions.TopologicalSortCycleException;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementación del algoritmo de Kahn para ordenar cuentas jerárquicamente.
 * Complejidad: O(V + E).
 *
 * Manejo de padres ausentes: si el parent_id de una cuenta no existe en el
 * conjunto de entrada, la cuenta se trata como raíz (in-degree 0).
 */
@Component
public class KahnTopologicalSort implements ITopologicalSortPort {

    /**
     * Ordena las cuentas de modo que cada padre aparezca antes que sus hijos.
     *
     * @param cuentas lista plana de cuentas
     * @return lista en orden topológico
     * @throws TopologicalSortCycleException si se detecta un ciclo
     */
    @Override
    public List<AccountCatalogueEntity> ordenar(List<AccountCatalogueEntity> cuentas) {
        if (cuentas == null || cuentas.isEmpty()) {
            return Collections.emptyList();
        }

        // Mapa id → entidad para resolver referencias
        Map<Long, AccountCatalogueEntity> porId = new HashMap<>();
        for (AccountCatalogueEntity c : cuentas) {
            porId.put(c.getId(), c);
        }

        // Calcular in-degree de cada nodo
        Map<Long, Integer> inDegree = new HashMap<>();
        // Mapa padre → lista de hijos (solo para nodos presentes en el conjunto)
        Map<Long, List<Long>> hijosDirectos = new HashMap<>();

        for (AccountCatalogueEntity c : cuentas) {
            inDegree.putIfAbsent(c.getId(), 0);
            hijosDirectos.putIfAbsent(c.getId(), new ArrayList<>());
        }

        for (AccountCatalogueEntity c : cuentas) {
            Long parentId = resolverParentId(c);
            if (parentId != null && porId.containsKey(parentId)) {
                // El padre está en el conjunto: incrementar in-degree del hijo
                inDegree.merge(c.getId(), 1, Integer::sum);
                hijosDirectos.get(parentId).add(c.getId());
            }
            // Si el padre NO está en el conjunto, el nodo queda con in-degree 0 (raíz efectiva)
        }

        // Kahn: inicializar cola con nodos de in-degree 0
        Queue<Long> cola = new LinkedList<>();
        for (Map.Entry<Long, Integer> e : inDegree.entrySet()) {
            if (e.getValue() == 0) {
                cola.add(e.getKey());
            }
        }

        List<AccountCatalogueEntity> resultado = new ArrayList<>(cuentas.size());

        while (!cola.isEmpty()) {
            Long idActual = cola.poll();
            resultado.add(porId.get(idActual));

            for (Long hijoId : hijosDirectos.getOrDefault(idActual, Collections.emptyList())) {
                int nuevo = inDegree.merge(hijoId, -1, Integer::sum);
                if (nuevo == 0) {
                    cola.add(hijoId);
                }
            }
        }

        // Si no procesamos todos los nodos, hay un ciclo
        if (resultado.size() != cuentas.size()) {
            throw new TopologicalSortCycleException();
        }

        return resultado;
    }

    /**
     * Extrae el ID del padre de una entidad, o null si es raíz.
     */
    private Long resolverParentId(AccountCatalogueEntity cuenta) {
        if (cuenta.getParent() == null) {
            return null;
        }
        return cuenta.getParent().getId();
    }
}
