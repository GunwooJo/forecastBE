package site.gunwoo.forecastBE.dto.region;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RegionDTO {

    private String r1;

    private String r2;

    private String r3;

    private Short xPos;

    private Short yPos;
}
