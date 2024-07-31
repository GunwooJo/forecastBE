package site.gunwoo.forecastBE.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    private String name;

    @Column(name = "x_pos", nullable = false)
    private Short xPos;

    @Column(name = "y_pos", nullable = false)
    private Short yPos;

    @OneToMany(mappedBy = "region")
    @Setter
    private List<MemberRegion> memberRegions = new ArrayList<>();
}
