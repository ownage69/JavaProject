package com.library.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Unified API error response")
public class ApiErrorResponse {

    @Schema(
            description = "Time when the error occurred",
            example = "2026-03-24T14:21:18.923+03:00"
    )
    private OffsetDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "HTTP status reason", example = "Bad Request")
    private String error;

    @Schema(description = "Short error message", example = "Validation failed")
    private String message;

    @Schema(description = "Request path", example = "/api/books")
    private String path;

    @Schema(description = "Validation error details")
    private List<ValidationError> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Single validation error")
    public static class ValidationError {

        @Schema(description = "Field or parameter name", example = "isbn")
        private String field;

        @Schema(description = "Rejected value", example = " ")
        private Object rejectedValue;

        @Schema(description = "Validation message", example = "Isbn is required")
        private String message;
    }
}
