package com.account_catalogue.accounting.application.input;

import java.time.LocalDate;
import java.util.List;

import com.account_catalogue.accounting.domain.models.InvoiceReplica;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.InvoiceSyncDto;

/**
 * Puerto de entrada para orquestar la lógica de negocio relacionada con las facturas.
 */
public interface IInvoiceProcessInputPort {
      /**
     * Procesa la creación de una factura, principalmente para actualizar los saldos de las cuentas contables.
     * @param invoiceDto El DTO de la factura recibido del evento.
     */
    void processInvoiceCreation(InvoiceSyncDto invoiceDto);   

    /**
     * Actualiza la fecha de vencimiento de una factura específica.
     * @param invoiceId El ID de la factura a modificar (tipo Long, como en tu entidad).
     * @param newDueDate La nueva fecha de vencimiento.
     */
    void updateDueDate(Long invoiceId, LocalDate newDueDate);

     /**
     * Caso de uso para encontrar facturas con saldo pendiente de un cliente.
     * @param clientId El ID del cliente a consultar.
     * @return Lista de facturas con saldo pendiente.
     */
    List<InvoiceReplica> findPendingInvoicesByClientId(Long clientId);

    /**
     * Caso de uso para encontrar facturas de una empresa.
     * @param enterpriseId El ID de la empresa a consultar.
     * @return Lista de facturas de la empresa.
     */
    List<InvoiceReplica> findInvoicesByEnterpriseId(String enterpriseId);

    /**
     * Caso de uso para encontrar una factura por su id.
     * @param invoiceId El ID de la factura a consultar.
     * @return La factura encontrada o null si no existe.
     */
    InvoiceReplica findInvoiceById(Long invoiceId);


}
