package Exceptions;

public class InvalidEpicTaskIdException extends RuntimeException {
    public InvalidEpicTaskIdException(String message) {
        super(message);
    }
}