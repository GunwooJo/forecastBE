package site.gunwoo.forecastBE.exception;

import lombok.Getter;

/*
단기예보 API를 호출할 때 오류가 발생한 경우 다음 예외를 사용하세요.
 */
@Getter
public class ShortForecastApiException extends RuntimeException{

    private String errorCode;   //API 응답으로 받은 resultCode
    private String errorMsg;    //API 응답으로 받은 resultMsg

    public ShortForecastApiException() {
    }

    public ShortForecastApiException(String message, String errorCode, String errorMsg) {
        super(message);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ShortForecastApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShortForecastApiException(String message, String errorCode, String errorMsg, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
