package com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.InvoiceReplicaEntity;

@Repository
public interface IInvoiceRepository extends JpaRepository<InvoiceReplicaEntity, Long> {
    /**
     * Encuentra todas las facturas de un cliente específico (ThirdParty)
     * cuyo saldo pendiente sea mayor a cero.
     *
     * @param thirdId El ID del cliente.
     * @param pendingValue El valor contra el que se compara el saldo pendiente (normalmente cero).
     * @return Una lista de entidades de facturas con saldo pendiente.
     */
    List<InvoiceReplicaEntity> findByThirdIdAndPendingValueGreaterThan(Long thirdId, Long pendingValue);

    /**
     * Busca todas las entidades de factura cuyos IDs están en la lista proporcionada.
     */
    List<InvoiceReplicaEntity> findByIdIn(List<Long> ids);

    /**
     * Actualiza el estado de una lista de facturas a WRITTEN_OFF de forma masiva.
     * También pone el valor pendiente a 0.
     */
    @Modifying
    @Query("UPDATE InvoiceReplicaEntity e SET e.status = 'WRITTEN_OFF', e.pendingValue = 0 WHERE e.id IN :ids")
    void writeOffInvoicesByIds(@Param("ids") List<Long> ids);
}
