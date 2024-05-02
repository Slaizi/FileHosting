package ru.Bogachev.fileHosting.domain.exception;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(final String message) {
        super(message);
    }
}
