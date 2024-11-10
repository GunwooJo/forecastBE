package site.gunwoo.forecastBE.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void changeMember(Member member) {
        this.member = member;
        member.getAlerts().add(this);
    }

    @Column(nullable = false, length = 30)
    private String r1; // ex) 서울특별시

    @Column(length = 30)
    private String r2; // ex) 강남구

    @Column(length = 30)
    private String r3; // ex) 역삼1동

    @Column(name = "x_pos", nullable = false)
    private short nx;

    @Column(name = "y_pos", nullable = false)
    private short ny;

    @Builder
    public Alert(String r1, String r2, String r3, short nx, short ny) {
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.nx = nx;
        this.ny = ny;
    }
}
