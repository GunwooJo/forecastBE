package site.gunwoo.forecastBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.gunwoo.forecastBE.entity.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
