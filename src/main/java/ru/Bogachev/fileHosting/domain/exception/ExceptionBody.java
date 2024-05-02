package ru.Bogachev.fileHosting.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ExceptionBody {
    private String message;
    private Map<String, String> errors;

    public ExceptionBody(final String errorMessage) {
        this.message = errorMessage;
    }
}