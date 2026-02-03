package com.account_catalogue.accounting.application.input;

import com.account_catalogue.accounting.domain.models.MessageProcessingError;

public interface IMessageProcessingErrorQueryPort {
      /**
     * @brief Finds a message processing error by ID
     * @param id Error record identifier
     * @return Optional containing the error record if found
     */
    MessageProcessingError findById(Long id);
    
    /**
     * @brief Finds the most recent message processing error
     * @return Optional containing the latest error record if found
     */
    MessageProcessingError findLastRecord();
}
