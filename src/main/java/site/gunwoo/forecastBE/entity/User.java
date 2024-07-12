package site.gunwoo.forecastBE.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Builder
    public User(String email, String password) {
        Assert.hasText(email, "email은 필수입니다.");
        Assert.hasText(password, "password는 필수입니다.");
        this.email = email;
        this.password = password;
    }
}
