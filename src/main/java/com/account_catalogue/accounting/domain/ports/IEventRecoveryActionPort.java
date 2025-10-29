package com.account_catalogue.accounting.domain.ports;


//Documentación en español
/**
 * Puerto para definir acciones de recuperación cuando falla el procesamiento de eventos
 * Permite la implementación de estrategias de recuperación específicas para diferentes
 * tipos de eventos, proporcionando resiliencia a las fallas en el procesamiento de mensajes.
 */

/**
 * @brief Port for defining recovery actions when event processing fails
 * 
 * Enables implementation of specific recovery strategies for different
 * types of events, providing resilience to message processing failures.
 * 
 * @param <T> Type of event for which recovery is executed
 */
public interface IEventRecoveryActionPort<T> {
    
    /**
     * @brief Ejecuta la acción de recuperación cuando falla el procesamiento normal del evento
     * @param event el evento que no se pudo procesar
     * @return True si la recuperación fue exitosa, false en caso contrario
     */

    /**
     * @brief Executes recovery action when normal event processing fails
     * @param event The event that failed to be processed
     * @return True if recovery was successful, false otherwise
     */
    boolean executeRecoveryAction(T event);
    
    /**
     * @brief Determina si este puerto puede manejar la recuperación para el tipo de evento dado
     * @param event el evento a validar
     * @return True si puede manejar la recuperación, false en caso contrario
     */

    /**
     * @brief Determines if this port can handle recovery for the given event type
     * @param event The event to validate
     * @return True if can handle recovery, false otherwise
     */
    boolean canHandle(T event);
}
