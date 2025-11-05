package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.utils.ImportConstants;
import com.account_catalogue.commons.exceptions.catalogue.FileValidationException;
import com.account_catalogue.commons.exceptions.catalogue.FileSizeExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @brief Servicio para validación de archivos de importación
 *
 * Servicio para validación directa de archivos de importación.
 * Valida tamaño, extensión y contenido básico del archivo.
 */
@Slf4j
@Service
public class AccountCatalogueFileValidationService {

    /**
     * Valida el archivo de importación.
     * 
     * @param file archivo a validar
     * @throws FileValidationException si el archivo no es válido
     * @throws FileSizeExceededException si el archivo excede el tamaño máximo
     */
    public void validate(MultipartFile file) {
        validateNotNull(file);
        validateNotEmpty(file);
        validateSize(file);
        validateExtension(file);
    }

    /**
     * Valida que el archivo no sea null.
     */
    private void validateNotNull(MultipartFile file) {
        if (file == null) {
            throw FileValidationException.forNullFile();
        }
    }

    /**
     * Valida que el archivo no esté vacío.
     */
    private void validateNotEmpty(MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0) {
            throw FileValidationException.forEmptyFile(file.getOriginalFilename());
        }
    }

    /**
     * Valida que el tamaño del archivo no exceda el máximo permitido.
     */
    private void validateSize(MultipartFile file) {
        if (file.getSize() > ImportConstants.MAX_FILE_SIZE) {
            throw new FileSizeExceededException(ImportConstants.MAX_FILE_SIZE);
        }
    }

    /**
     * Valida que la extensión del archivo sea permitida (.xlsx o .xls).
     */
    private void validateExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw FileValidationException.forInvalidExtension(
                "archivo sin nombre", 
                ImportConstants.SUPPORTED_EXTENSIONS
            );
        }

        String lowerFilename = filename.toLowerCase();
        boolean validExtension = false;

        for (String ext : ImportConstants.SUPPORTED_EXTENSIONS) {
            if (lowerFilename.endsWith(ext)) {
                validExtension = true;
                break;
            }
        }

        if (!validExtension) {
            throw FileValidationException.forInvalidExtension(
                filename, 
                ImportConstants.SUPPORTED_EXTENSIONS
            );
        }
    }
}

