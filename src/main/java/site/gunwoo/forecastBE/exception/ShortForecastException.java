package site.gunwoo.forecastBE.exception;

public class ShortForecastException extends RuntimeException {

    public ShortForecastException() {
    }

    public ShortForecastException(String message) {
        super(message);
    }

    public ShortForecastException(String message, Throwable cause) {
        super(message, cause);
    }
}
