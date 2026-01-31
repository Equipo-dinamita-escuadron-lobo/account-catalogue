package com.account_catalogue.accounting.infraestructure.input;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.account_catalogue.accounting.application.input.IMessageProcessingErrorCommandPort;
import com.account_catalogue.accounting.application.input.IMessageProcessingErrorQueryPort;
import com.account_catalogue.accounting.domain.models.MessageProcessingError;
import com.account_catalogue.accounting.infraestructure.input.data.response.ApiResponse;
import com.account_catalogue.accounting.infraestructure.input.data.response.MessageProcessingErrorResponse;
import com.account_catalogue.accounting.infraestructure.input.mapper.IMessageProcessingErrorRestMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accountCatalogue/accounting/message-processing-errors")
public class MessageProcessingErrorController {
    private final IMessageProcessingErrorQueryPort queryUseCase;
    private final IMessageProcessingErrorCommandPort commandUseCase;
    private final IMessageProcessingErrorRestMapper restMapper;

    /**
     * @brief Retrieves the most recent message processing error
     * @return Response with the latest message processing error or not found
     */
    @GetMapping("/last")
    public ResponseEntity<ApiResponse<MessageProcessingErrorResponse>> findLastRecord() {
        MessageProcessingError error = queryUseCase.findLastRecord();

        if (error == null) {
            return ResponseEntity.ok(
                    ApiResponse.successEmpty("No message processing errors found.",
                            "NO_CONTENT"));
        }

        MessageProcessingErrorResponse responseDto = restMapper.toResponse(error);
        return ResponseEntity.ok(
                ApiResponse.success(responseDto, "Latest message processing error found successfully."));
    }

    /**
     * @brief Deletes all message processing error records
     * @return Response confirming deletion
     */
    @DeleteMapping("/delete-all")
    public ResponseEntity<ApiResponse<Void>> deleteAll() {
        commandUseCase.deleteAll();
        return ResponseEntity.ok(ApiResponse.success(null, "All message processing errors deleted successfully."));
    }
}
