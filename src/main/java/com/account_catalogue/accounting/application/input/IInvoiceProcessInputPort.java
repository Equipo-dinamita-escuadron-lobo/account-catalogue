package com.account_catalogue.accounting.application.input;

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
}
