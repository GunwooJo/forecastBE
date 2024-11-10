package site.gunwoo.forecastBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.gunwoo.forecastBE.entity.Region;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    @Query("SELECT DISTINCT r.nx, r.ny FROM Region r")
    List<Object[]> findDistinctByNxAndNy();

    Optional<Region> findByR1AndR2AndR3AndNxAndNy(String r1, String r2, String r3, short nx, short ny);
}
