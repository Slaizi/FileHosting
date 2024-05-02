package ru.Bogachev.fileHosting.domain.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException(final String message) {
        super(message);
    }
}
