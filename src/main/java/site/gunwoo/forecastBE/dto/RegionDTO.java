package site.gunwoo.forecastBE.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RegionDTO {

    private String name;

    private Short xPos;

    private Short yPos;
}
