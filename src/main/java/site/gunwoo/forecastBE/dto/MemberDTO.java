package site.gunwoo.forecastBE.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.NONE)
public class MemberDTO {

    @NotBlank
    @Email
    private String email;

    @NotEmpty
    private String password;

    @Builder
    public MemberDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
