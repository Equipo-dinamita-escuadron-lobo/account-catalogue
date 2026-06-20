package com.account_catalogue.copy.domain.exceptions;

/**
 * Se lanza cuando una cuenta referencia un parent_id que no existe en entOrigen.
 */
public class ParentAccountNotFoundException extends RuntimeException {

    public ParentAccountNotFoundException(Long parentId) {
        super("Cuenta padre no encontrada en entOrigen: id=" + parentId);
    }
}
