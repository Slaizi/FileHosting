package ru.Bogachev.fileHosting.domain.exception;

public class MinioCreateBucketException extends RuntimeException {
    public MinioCreateBucketException(final String message) {
        super(message);
    }
}
