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

    @NotBlank
    @Email
    private String email;

    @NotEmpty
    private String password;

    @Builder
    public MemberJoinDTO(String email, String password, List<String> regions) {
        this.email = email;
        this.password = password;
    }
}
