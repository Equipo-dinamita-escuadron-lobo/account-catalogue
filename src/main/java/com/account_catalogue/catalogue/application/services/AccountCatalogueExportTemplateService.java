package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueExportTemplateInputPort;
import com.account_catalogue.commons.exceptions.catalogue.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Servicio para la exportación de plantilla del catálogo de cuentas.
 */
@Service
@Slf4j
public class AccountCatalogueExportTemplateService implements IAccountCatalogueExportTemplateInputPort {

    private static final String TEMPLATE_PATH = "templates/plantillaCatalogoCuentas.xlsx";

    /**
     * Obtiene la plantilla de catálogo de cuentas para descarga.
     * 
     * @return Resource con la plantilla de Excel
     * @throws TemplateNotFoundException si la plantilla no se encuentra
     */
    @Override
    public Resource getAccountCatalogueTemplate() {
        try {
            Resource resource = new ClassPathResource(TEMPLATE_PATH);
            
            if (!resource.exists()) {
                log.error("La plantilla no se encontró en la ruta: {}", TEMPLATE_PATH);
                throw new TemplateNotFoundException();
            }
            
            if (!resource.isReadable()) {
                log.error("La plantilla no es legible en la ruta: {}", TEMPLATE_PATH);
                throw new TemplateNotFoundException();
            }
            
            log.info("Plantilla de catálogo de cuentas obtenida exitosamente");
            return resource;
            
        } catch (Exception e) {
            log.error("Error al obtener la plantilla de catálogo de cuentas: {}", e.getMessage());
            throw new TemplateNotFoundException();
        }
    }
}
