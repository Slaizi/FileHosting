package ru.Bogachev.fileHosting.web.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.Bogachev.fileHosting.domain.exception.AccessDeniedException;
import ru.Bogachev.fileHosting.domain.exception.ExceptionBody;
import ru.Bogachev.fileHosting.domain.exception.UserNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMethodIllegalState(
            final IllegalStateException e
    ) {
        return new ExceptionBody(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody handleUserNotFound(
            final UserNotFoundException e
    ) {
        return new ExceptionBody(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionBody handleAccessDenied() {
        return new ExceptionBody("Access denied.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMethodArgumentNotValid(
            final MethodArgumentNotValidException e
    ) {
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed.");
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        exceptionBody.setErrors(errors.stream()
                .collect(Collectors.toMap(FieldError::getField,
                        Objects.requireNonNull(FieldError::getDefaultMessage))
                )
        );
        return exceptionBody;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleConstraintViolation(
            final ConstraintViolationException e
    ) {
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed.");
        exceptionBody.setErrors(e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                )));
        return exceptionBody;
    }
}
