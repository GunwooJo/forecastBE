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

    @Column(length = 30, nullable = false, unique = true)
    private String name;

    @Column(name = "x_pos", nullable = false)
    private Short xPos;

    @Column(name = "y_pos", nullable = false)
    private Short yPos;
}
