package site.gunwoo.forecastBE.exception;

public class CustomServerException extends RuntimeException{
    public CustomServerException() {
    }

    public CustomServerException(String message) {
        super(message);
    }

    public CustomServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
