package com.account_catalogue.copy.application.services;

import com.account_catalogue.copy.application.input.ICleanupCopyPort;
import com.account_catalogue.copy.application.output.ICopyJobLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio para eliminar los registros de log de un proceso de copia terminado.
 * REQ-CONTRACT-01 (DELETE /{idProceso}/cleanup).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CleanupCopyService implements ICleanupCopyPort {

    private final ICopyJobLogRepositoryPort logRepo;

    @Override
    public void limpiar(String idProceso) {
        log.info("Limpiando registros de copia para proceso {}", idProceso);
        logRepo.eliminarPorIdProceso(idProceso);
    }
}
