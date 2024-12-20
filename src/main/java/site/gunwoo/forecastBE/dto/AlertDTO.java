package site.gunwoo.forecastBE.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AlertDTO {

    private Long id;

    private String r1; // ex) 서울특별시

    private String r2; // ex) 강남구

    private String r3; // ex) 역삼1동

    private short nx;

    private short ny;
}
