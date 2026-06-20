package com.account_catalogue.copy.application.services;

import com.account_catalogue.copy.application.input.ICancelCopyPort;
import com.account_catalogue.copy.application.output.ICopyJobLogRepositoryPort;
import com.account_catalogue.copy.domain.enums.CopyJobState;
import com.account_catalogue.copy.domain.exceptions.CopyJobNotFoundException;
import com.account_catalogue.copy.domain.models.CopyJobLog;
import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.CopyCancelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Servicio para cancelar un proceso de copia en curso.
 * REQ-CONTRACT-01 (POST /{idProceso}/cancel).
 */
@Service
@RequiredArgsConstructor
public class CancelCopyService implements ICancelCopyPort {

    private final ICopyJobLogRepositoryPort logRepo;

    @Override
    public CopyCancelResponseDto cancelar(String idProceso) {
        CopyJobLog log = logRepo.buscarPorIdProceso(idProceso)
                .orElseThrow(() -> new CopyJobNotFoundException(idProceso));

        CopyJobLog cancelado = CopyJobLog.builder()
                .idProceso(log.getIdProceso())
                .fase(log.getFase())
                .modulo(log.getModulo())
                .estado(CopyJobState.CANCELADO)
                .fechaInicio(log.getFechaInicio())
                .fechaFin(Instant.now())
                .equivalenciasGeneradas(log.getEquivalenciasGeneradas())
                .build();
        logRepo.guardar(cancelado);

        return CopyCancelResponseDto.builder()
                .estado(CopyJobState.CANCELADO.name())
                .build();
    }
}
