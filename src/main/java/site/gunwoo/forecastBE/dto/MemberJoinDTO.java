package site.gunwoo.forecastBE.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.NONE)
public class MemberJoinDTO {

    @NotBlank(message = "이메일은 필수 값입니다.")
    @Email
    private String email;

    @NotEmpty(message = "비밀번호는 필수 값입니다.")
    private String password;

    @NotEmpty(message = "알림을 받을 지역을 선택해야 합니다.")
    private List<AlertDTO> alerts = new ArrayList<>();

//    @NotEmpty(message = "지역을 선택해야 합니다.")
//    private List<RegionNameDTO> regions = new ArrayList<>();

    @Builder
    public MemberJoinDTO(String email, String password, List<AlertDTO> alerts) {
        this.email = email;
        this.password = password;
        this.alerts = alerts;
    }
}
