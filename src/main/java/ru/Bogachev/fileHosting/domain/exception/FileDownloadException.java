package ru.Bogachev.fileHosting.domain.exception;

public class FileDownloadException extends RuntimeException {
    public FileDownloadException(final String message) {
        super(message);
    }
}
