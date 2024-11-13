package site.gunwoo.forecastBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.gunwoo.forecastBE.entity.Alert;

import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("select a from Alert a join fetch a.member m where a.id = :id")
    Optional<Alert> findByIdWithMember(Long id);
}
