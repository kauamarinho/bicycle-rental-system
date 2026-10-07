package org.example.domain.exception;

public class InvalidCepException extends RuntimeException {

    public InvalidCepException(String message) {
        super(message);
    }
}
