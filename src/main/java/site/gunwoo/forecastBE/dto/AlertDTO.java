package site.gunwoo.forecastBE.dto;

import lombok.Getter;

@Getter
public class AlertDTO {

    private String r1; // ex) 서울특별시

    private String r2; // ex) 강남구

    private String r3; // ex) 역삼1동

    private short nx;

    private short ny;
}
