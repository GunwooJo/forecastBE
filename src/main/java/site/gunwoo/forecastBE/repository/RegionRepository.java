package site.gunwoo.forecastBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.gunwoo.forecastBE.entity.Region;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Region findByName(String name);

    @Query("SELECT DISTINCT r.xPos, r.yPos FROM Region r")
    List<Object[]> findDistinctByXPosAndYPos();
}
