package site.gunwoo.forecastBE.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MailDTO {

    private String address;
    private String title;
    private String message;
}
