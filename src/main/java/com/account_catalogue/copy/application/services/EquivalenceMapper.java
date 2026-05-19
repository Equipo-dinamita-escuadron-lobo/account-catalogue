package com.account_catalogue.copy.application.services;

import com.account_catalogue.copy.domain.models.CopyEquivalencia;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Colecta y consulta equivalencias (idViejo → idNuevo) por tabla/módulo.
 * Instancia con scope de request/ejecución — NO es singleton en el sentido de
 * que el servicio orquestador crea una instancia nueva por ejecución de fase.
 *
 * Nota: declarado @Component para que Spring pueda inyectarlo en prototype si se necesita,
 * pero el servicio principal instancia uno nuevo por llamada.
 */
@Component
public class EquivalenceMapper {

    /** Clave: tabla + ":" + idViejo → idNuevo */
    private final Map<String, Long> mapa = new HashMap<>();

    /**
     * Registra la equivalencia idViejo → idNuevo para la tabla dada.
     */
    public void registrar(String tabla, Long idViejo, Long idNuevo) {
        mapa.put(clave(tabla, idViejo), idNuevo);
    }

    /**
     * Resuelve el nuevo ID para el viejo ID en la tabla dada.
     *
     * @return el nuevo ID, o null si no hay equivalencia registrada
     */
    public Long resolverNuevoId(String tabla, Long idViejo) {
        return mapa.get(clave(tabla, idViejo));
    }

    /**
     * Retorna todas las equivalencias como lista de modelos de dominio.
     */
    public List<CopyEquivalencia> toList() {
        List<CopyEquivalencia> lista = new ArrayList<>(mapa.size());
        for (Map.Entry<String, Long> e : mapa.entrySet()) {
            // clave = tabla:idViejo
            String[] partes = e.getKey().split(":", 2);
            lista.add(CopyEquivalencia.builder()
                    .tabla(partes[0])
                    .idViejo(partes[1])
                    .idNuevo(String.valueOf(e.getValue()))
                    .build());
        }
        return lista;
    }

    /**
     * Limpia todas las equivalencias registradas (para reutilización).
     */
    public void limpiar() {
        mapa.clear();
    }

    private String clave(String tabla, Long idViejo) {
        return tabla + ":" + idViejo;
    }
}
