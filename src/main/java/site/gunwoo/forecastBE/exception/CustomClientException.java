package site.gunwoo.forecastBE.exception;

public class CustomClientException extends RuntimeException{
    public CustomClientException() {
    }

    public CustomClientException(String message) {
        super(message);
    }

    public CustomClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
