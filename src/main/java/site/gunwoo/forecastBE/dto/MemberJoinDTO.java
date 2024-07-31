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

    @NotEmpty
    private List<String> regions = new ArrayList<>();

    @Builder
    public MemberJoinDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
