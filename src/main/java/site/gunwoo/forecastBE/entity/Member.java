package site.gunwoo.forecastBE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<MemberRegion> memberRegions = new ArrayList<>();

    public void changeMemberRegions(List<MemberRegion> memberRegions) {
        memberRegions.forEach(memberRegion -> memberRegion.setMember(this));
        this.memberRegions = memberRegions;
    }

    @Builder
    public Member(String email, String password, List<MemberRegion> memberRegions) {
        this.email = email;
        this.password = password;
    }
}
