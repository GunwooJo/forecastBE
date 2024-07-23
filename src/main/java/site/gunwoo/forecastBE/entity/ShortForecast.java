package site.gunwoo.forecastBE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortForecast extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "short_forecast_id")
    private Long id;

    private LocalDate baseDate; //발표일자
    private LocalTime baseTime; //발표시각
    private String category;    //자료구분문자
    private LocalDate fcstDate; //예보일자
    private LocalTime fcstTime; //예보시각
    private int fcstValue;      //예보 값
    private int nx;             //예보지점 X 좌표
    private int ny;             //예보지점 Y 좌표


    @Builder
    public ShortForecast(LocalDate baseDate, LocalTime baseTime, String category, LocalDate fcstDate, LocalTime fcstTime, int fcstValue, int nx, int ny) {
        this.baseDate = baseDate;
        this.baseTime = baseTime;
        this.category = category;
        this.fcstDate = fcstDate;
        this.fcstTime = fcstTime;
        this.fcstValue = fcstValue;
        this.nx = nx;
        this.ny = ny;
    }
}
