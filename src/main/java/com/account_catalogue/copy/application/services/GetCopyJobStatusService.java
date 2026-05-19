package com.account_catalogue.copy.application.services;

import com.account_catalogue.copy.application.input.IGetCopyJobStatusPort;
import com.account_catalogue.copy.application.output.ICopyJobLogRepositoryPort;
import com.account_catalogue.copy.domain.exceptions.CopyJobNotFoundException;
import com.account_catalogue.copy.domain.models.CopyJobLog;
import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.CopyStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio para consultar el estado de un proceso de copia.
 * REQ-CONTRACT-01 (GET /{idProceso}/status).
 */
@Service
@RequiredArgsConstructor
public class GetCopyJobStatusService implements IGetCopyJobStatusPort {

    private final ICopyJobLogRepositoryPort logRepo;

    @Override
    public CopyStatusResponseDto obtenerEstado(String idProceso) {
        CopyJobLog log = logRepo.buscarPorIdProceso(idProceso)
                .orElseThrow(() -> new CopyJobNotFoundException(idProceso));

        return CopyStatusResponseDto.builder()
                .fase(log.getFase() != null ? log.getFase() : 0)
                .estado(log.getEstado() != null ? log.getEstado().name() : "DESCONOCIDO")
                .registrosProcesados(log.getEquivalenciasGeneradas() != null ? log.getEquivalenciasGeneradas() : 0)
                .intentos(1)
                .ultimoError(log.getErrorMessage())
                .build();
    }
}
