package site.gunwoo.forecastBE.dto;

import lombok.Getter;
import site.gunwoo.forecastBE.entity.ShortForecast;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class SimpleShortForecastDTO {

    private LocalDate baseDate;
    private LocalTime baseTime;
    private String category;
    private LocalDate fcstDate;
    private LocalTime fcstTime;
    private String fcstValue;
    private int nx;
    private int ny;

    public SimpleShortForecastDTO(ShortForecast s) {
        this.baseDate = s.getBaseDate();
        this.baseTime = s.getBaseTime();
        this.category = s.getCategory();
        this.fcstDate = s.getFcstDate();
        this.fcstTime = s.getFcstTime();
        this.fcstValue = s.getFcstValue();
        this.nx = s.getNx();
        this.ny = s.getNy();
    }
}
