package ru.Bogachev.fileHosting.domain.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Schema(description = "Exception Body")
public class ExceptionBody {

    @Schema(
            description = "message",
            example = "error message"
    )
    private String message;

    @Schema(
            description = "errors"
    )
    private Map<String, String> errors;

    public ExceptionBody(final String errorMessage) {
        this.message = errorMessage;
    }
}
