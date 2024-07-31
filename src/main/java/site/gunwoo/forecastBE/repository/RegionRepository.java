package site.gunwoo.forecastBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.gunwoo.forecastBE.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Region findByName(String name);
}
