package com.account_catalogue.copy.application.input;

/**
 * Puerto de entrada para limpiar los registros temporales de un proceso de copia terminado.
 */
public interface ICleanupCopyPort {

    /**
     * Elimina los registros de log del proceso indicado.
     *
     * @param idProceso UUID del proceso
     */
    void limpiar(String idProceso);
}
