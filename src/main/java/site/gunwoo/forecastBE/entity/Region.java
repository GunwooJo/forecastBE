package site.gunwoo.forecastBE.entity;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String r1; // ex) 서울특별시

    @Column(length = 30)
    private String r2; // ex) 강남구

    @Column(length = 30)
    private String r3; // ex) 역삼1동

    @Column(name = "x_pos", nullable = false)
    private Short xPos;

    @Column(name = "y_pos", nullable = false)
    private Short yPos;
}
