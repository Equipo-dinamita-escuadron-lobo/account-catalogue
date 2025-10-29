package com.account_catalogue.accounting.infraestructure.output.messageBroker.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.account_catalogue.accounting.domain.models.InvoiceReplica;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.InvoiceReplicaEntity;



public interface IInvoicePersistenceMapper {
    //Metodo para pasar a domain
    BigDecimal toDomain(BigDecimal balance);

    //Metodo para pasar listado de entidades a listado de domain
    List<InvoiceReplica> toInvoiceReplicaList(List<InvoiceReplicaEntity> invoiceEntityList);
}
