package Exceptions;

public class BadUrlException extends RuntimeException {
    public BadUrlException(String message) {
        super(message);
    }
}
