package site.gunwoo.forecastBE.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ShortForecastRequest {

    @NotBlank
    private String baseDate;

    @NotBlank
    private String baseTime;

    @NotNull
    @Min(value = 1)
    private int numOfRows;

    @NotNull
    @Min(value = 1)
    private int pageNo;

    @NotNull
    private int nx;

    @NotNull
    private int ny;
}
