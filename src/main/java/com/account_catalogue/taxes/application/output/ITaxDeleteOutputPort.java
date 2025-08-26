package com.account_catalogue.taxes.application.output;

public interface ITaxDeleteOutputPort {
    
    /**
     * Realiza soft delete de un impuesto por su ID y empresa.
     * 
     * @param id el ID del impuesto
     * @param idEnterprise el ID de la empresa
     * @return true si se eliminó con éxito, false de lo contrario
     */
    boolean deleteByCode(long id, String idEnterprise);
}
