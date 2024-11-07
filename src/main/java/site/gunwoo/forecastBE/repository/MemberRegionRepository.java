package site.gunwoo.forecastBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.gunwoo.forecastBE.entity.MemberRegion;

import java.util.List;

public interface MemberRegionRepository extends JpaRepository<MemberRegion, Long> {

    @Query("select mr from MemberRegion mr" +
            " join fetch mr.region r")
    List<MemberRegion> findAllWithRegion();
}
